# Evaluación 02 – Guía de cambios y capturas

Proyecto: `admision` (Hospital San Marcos) · Spring Boot 4 + JPA/Hibernate + MySQL + Spring Security + AOP

## Antes de empezar

1. Enciende **MySQL en XAMPP**.
2. Ejecuta `AdmisionApplication` desde IntelliJ (puerto **8081**).
3. Abre `http://localhost:8081` (te enviará al login).

**Usuarios de prueba** (se crean solos al iniciar, en `config/DataInitializer.java`):

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | ADMINISTRADOR |
| medico | medico123 | MEDICO |
| recepcion | recepcion123 | RECEPCIONISTA |

> Rutas de código: todas parten de `admision/src/main/java/com/hospital/admision/`.
> Rutas web: parten de `admision/src/main/resources/static/`.

---

## Cambio 0 – Dependencias (`pom.xml`)

Se agregaron `spring-boot-starter-security` (P5) y `spring-boot-starter-aspectj` (P2, AOP).
📸 **Captura:** el bloque de las dos dependencias nuevas en `pom.xml`.

---

## Pregunta 1 – Relaciones con Hibernate/JPA

### Código
| Archivo | Qué capturar |
|---|---|
| `model/Paciente.java` | `@OneToMany(mappedBy = "paciente", cascade = ALL, orphanRemoval = true)` + `@JsonIgnore` |
| `model/Admision.java` | `@ManyToOne` + `@JoinColumn(name="paciente_id")`, `@OneToOne` con Acompañante (`acompanante_id`) y con Triaje |
| `model/Triaje.java` | `@OneToOne` + `@JoinColumn(name="admision_id", unique=true)` |
| `model/Usuario.java` | `@ManyToOne` + `@JoinColumn(name="rol_id")` (relación Usuario–Rol) |
| `model/Rol.java` | `@OneToMany(mappedBy = "rol")` |

Resumen de relaciones:
- Paciente **1 : N** Admisión (`@OneToMany` / `@ManyToOne`)
- Admisión **1 : 1** Acompañante (`@OneToOne`)
- Admisión **1 : 1** Triaje (`@OneToOne`)
- Rol **1 : N** Usuario (`@OneToMany` / `@ManyToOne`)

### CRUD con relaciones (nuevo)
| Archivo | Qué capturar |
|---|---|
| `service/impl/PacienteServiceImpl.java` | `actualizarPaciente` (valida DNI repetido) y `eliminarPaciente` (no deja eliminar si hay admisión ABIERTA; si no, borra en cascada) |
| `service/impl/AdmisionServiceImpl.java` | `listarPorPaciente`, `eliminarAdmision`, validación "una admisión solo tiene un triaje" |
| `controller/PacienteController.java` | `GET /{id}`, `GET /{id}/admisiones`, `PUT /{id}`, `DELETE /{id}` |
| `controller/AdmisionController.java` | `DELETE /api/admisiones/{id}` |

### Base de datos (SQLyog / phpMyAdmin)
📸 Captura de cada tabla con sus claves foráneas:
```sql
SHOW CREATE TABLE admisiones;   -- FK paciente_id y acompanante_id
SHOW CREATE TABLE triajes;      -- FK admision_id (UNIQUE)
SHOW CREATE TABLE usuarios;     -- FK rol_id
```
También sirve el **diagrama** (Schema Designer en SQLyog o Designer en phpMyAdmin).

### Funcionamiento (entra como `recepcion` o `admin`)
1. Pestaña **1. Registro Paciente** → registra un paciente 📸
2. Pestaña **2. Admisión** → registra una admisión con acompañante para ese ID 📸
3. Pestaña **5. Pacientes** → botón **Admisiones** (muestra la relación 1:N) 📸
4. **Editar** un paciente 📸
5. **Eliminar** un paciente con admisión ABIERTA → sale el error de integridad 📸
6. Dar de alta la admisión y eliminar el paciente → se borra con sus admisiones 📸

---

## Pregunta 2 – Auditoría (AOP)

### Código
| Archivo | Qué capturar |
|---|---|
| `model/Auditoria.java` | Entidad con usuario, fechaHora, operacion, entidad, registroId, detalle |
| `repository/AuditoriaRepository.java` | Consultas ordenadas por fecha |
| `auditoria/Auditable.java` | Anotación personalizada `@Auditable(operacion, entidad)` |
| `auditoria/AuditoriaAspect.java` | **Clave:** `@Aspect` + `@AfterReturning`: toma el usuario de Spring Security y guarda el registro automáticamente |
| Cualquier servicio (ej. `PacienteServiceImpl`) | Los métodos marcados con `@Auditable(operacion = "REGISTRO", entidad = "Paciente")` |
| `controller/AuditoriaController.java` | `GET /api/auditoria` (solo ADMINISTRADOR) |

Operaciones auditadas: **REGISTRO**, **MODIFICACION**, **ELIMINACION** y **CAMBIO_ESTADO** sobre Paciente, Admision, Triaje, Usuario y Rol.

### Evidencia
1. Haz operaciones con distintos usuarios (registro, edición y eliminación).
2. Entra como `admin` → menú **Auditoría** 📸 (se ve qué usuario hizo cada cosa).
3. En la BD: `SELECT * FROM auditoria ORDER BY id DESC;` 📸

---

## Pregunta 3 – Gestión de usuarios y roles

### Código
| Archivo | Qué capturar |
|---|---|
| `model/Rol.java`, `model/Usuario.java` | Entidades y relación `@ManyToOne` |
| `model/dto/UsuarioDTO.java` | La contraseña es `WRITE_ONLY` (nunca se devuelve) |
| `repository/RolRepository.java`, `repository/UsuarioRepository.java` | |
| `service/impl/RolServiceImpl.java` | Registrar, editar, activar/desactivar; no deja desactivar ADMINISTRADOR |
| `service/impl/UsuarioServiceImpl.java` | Contraseña cifrada con **BCrypt**, asignar rol, activar/desactivar, no permite desactivarse a sí mismo |
| `controller/RolController.java`, `controller/UsuarioController.java` | Endpoints GET, POST, PUT, PATCH `/estado` |
| `config/DataInitializer.java` | Crea ADMINISTRADOR, MEDICO y RECEPCIONISTA |
| `exception/GlobalExceptionHandler.java` | Devuelve los errores como `{ "mensaje": ... }` |

### Base de datos
```sql
SELECT * FROM roles;
SELECT id, username, password, nombre_completo, activo, rol_id FROM usuarios;   -- se ve el hash BCrypt
SELECT u.username, r.nombre AS rol FROM usuarios u JOIN roles r ON r.id = u.rol_id;
```
📸 Captura de las tres consultas.

---

## Pregunta 4 – Frontend de usuarios y roles

| Archivo nuevo | Descripción |
|---|---|
| `static/usuarios.html` | Formulario + listado + edición + selector de rol + activar/desactivar |
| `static/roles.html` | Formulario + listado + edición + activar/desactivar |
| `static/auditoria.html` | Bitácora con filtro por entidad |
| `static/login.html` | Pantalla de inicio de sesión |
| `static/js/sesion.js` | Arma el menú según el rol, botón Salir y llamadas a la API |
| `static/css/hospital.css` | Estilos compartidos (se movieron desde `index.html`) |

### Capturas (como `admin`)
**Usuarios**
1. Formulario vacío y listado 📸
2. Registrar un usuario nuevo (ej. `jperez` con rol RECEPCIONISTA) 📸 mensaje verde
3. **Editar** → cambiar nombre y rol 📸
4. **Desactivar** 📸 (la fila queda gris y dice INACTIVO)
5. Cierra sesión e intenta entrar con ese usuario → *"Su usuario o su rol se encuentra desactivado"* 📸

**Roles**
1. Listado con la columna USUARIOS 📸
2. Registrar un rol nuevo (ej. `Enfermero`, se guarda como `ENFERMERO`) 📸
3. **Editar** su descripción 📸
4. **Desactivar** 📸 → en Usuarios aparece "(inactivo)" y no se puede asignar
5. Intentar desactivar ADMINISTRADOR → mensaje de error 📸

---

## Pregunta 5 – Control de acceso según el rol

### Código
| Archivo | Qué capturar |
|---|---|
| `config/SecurityConfig.java` | **Clave:** reglas `requestMatchers(...).hasRole(...)`, `formLogin`, `redireccionPorRol()`, `logout`, respuestas 401/403 |
| `config/UsuarioDetailsServiceImpl.java` | Carga el usuario desde MySQL; bloquea si el usuario o su rol está inactivo |
| Controladores `Usuario`, `Rol`, `Auditoria` | `@PreAuthorize("hasRole('ADMINISTRADOR')")` |
| `controller/AuthController.java` | `GET /api/auth/me` (datos del usuario logueado) |
| `static/js/sesion.js` | `MENU` con los roles de cada opción y el uso de `data-roles` |
| `static/index.html` | Pestañas con `data-roles="ADMINISTRADOR,RECEPCIONISTA"`, etc. |

### Tabla de permisos implementada

| Rol | Accede a |
|---|---|
| ADMINISTRADOR | Usuarios, Roles, Auditoría y todo el panel clínico |
| MEDICO | Triaje, Sala de espera (dar alta), consulta de Pacientes |
| RECEPCIONISTA | Registro de Pacientes, Admisiones (ingresos), Sala de espera (solo ver), Pacientes (editar/eliminar) |

### Capturas
1. **Login** 📸, y login con contraseña incorrecta 📸
2. **Redirección después del login:**
   - `admin` → llega a **Usuarios** 📸 (menú con Usuarios, Roles, Auditoría)
   - `medico` → llega a **Triaje** 📸 (solo ve pestañas 3, 4 y 5; no ve Usuarios)
   - `recepcion` → llega a **Registro Paciente** 📸 (no ve Triaje; en Sala de Espera dice "Sin permiso")
3. **Bloqueo de acceso no autorizado:** como `medico`, escribe en la barra `http://localhost:8081/usuarios.html` → regresa al panel con *"No tiene permisos para acceder a esa opción"* 📸
4. **Validación en el backend:** como `medico`, abre `http://localhost:8081/api/usuarios` → `{"mensaje":"No tiene permisos para realizar esta operación."}` (403) 📸
5. Sin sesión, abre `http://localhost:8081/api/pacientes` → `{"mensaje":"Debe iniciar sesión."}` (401) 📸
6. Botón **Salir** → *"Sesión cerrada correctamente"* 📸
