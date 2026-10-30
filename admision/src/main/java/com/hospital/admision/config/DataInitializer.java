package com.hospital.admision.config;

import com.hospital.admision.model.Rol;
import com.hospital.admision.model.Usuario;
import com.hospital.admision.repository.RolRepository;
import com.hospital.admision.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Al iniciar la aplicación crea los roles base y un usuario de prueba por rol,
 * solo si todavía no existen en la base de datos.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = crearRolSiNoExiste("ADMINISTRADOR", "Gestiona usuarios, roles, auditoría y todos los módulos");
        Rol medico = crearRolSiNoExiste("MEDICO", "Consulta pacientes, registra triaje y altas médicas");
        Rol recepcion = crearRolSiNoExiste("RECEPCIONISTA", "Registra pacientes y admisiones");

        crearUsuarioSiNoExiste("admin", "admin123", "Administrador del Sistema", "admin@hospital.pe", admin);
        crearUsuarioSiNoExiste("medico", "medico123", "Dr. Carlos Mendoza", "cmendoza@hospital.pe", medico);
        crearUsuarioSiNoExiste("recepcion", "recepcion123", "Ana Torres", "atorres@hospital.pe", recepcion);
    }

    private Rol crearRolSiNoExiste(String nombre, String descripcion) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(nombre, descripcion)));
    }

    private void crearUsuarioSiNoExiste(String username, String password, String nombre, String email, Rol rol) {
        if (usuarioRepository.existsByUsername(username)) return;
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNombreCompleto(nombre);
        usuario.setEmail(email);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
