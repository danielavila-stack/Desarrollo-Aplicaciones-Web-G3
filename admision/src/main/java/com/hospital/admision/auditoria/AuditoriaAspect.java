package com.hospital.admision.auditoria;

import com.hospital.admision.model.Auditoria;
import com.hospital.admision.repository.AuditoriaRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Aspecto (AOP) que intercepta los métodos anotados con @Auditable y,
 * cuando terminan sin errores, guarda un registro en la tabla "auditoria".
 */
@Aspect
@Component
public class AuditoriaAspect {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaAspect(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "resultado")
    public void registrarOperacion(JoinPoint joinPoint, Auditable auditable, Object resultado) {
        Auditoria registro = new Auditoria();
        registro.setUsuario(obtenerUsuarioActual());
        registro.setFechaHora(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        registro.setOperacion(auditable.operacion());
        registro.setEntidad(auditable.entidad());
        registro.setRegistroId(obtenerIdRegistro(resultado, joinPoint.getArgs()));
        registro.setDetalle("Método: " + joinPoint.getSignature().getName());
        auditoriaRepository.save(registro);
    }

    // Usuario autenticado en Spring Security (o SISTEMA si la operación es interna)
    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "SISTEMA";
        }
        return auth.getName();
    }

    // El ID se toma del objeto devuelto (getId) o, si no hay, del primer argumento Long (ej. eliminar)
    private Long obtenerIdRegistro(Object resultado, Object[] args) {
        if (resultado != null) {
            try {
                Method getId = resultado.getClass().getMethod("getId");
                Object id = getId.invoke(resultado);
                if (id instanceof Long) return (Long) id;
            } catch (Exception ignored) {
                // El resultado no tiene getId(); se intenta con los argumentos
            }
        }
        for (Object arg : args) {
            if (arg instanceof Long) return (Long) arg;
        }
        return null;
    }
}
