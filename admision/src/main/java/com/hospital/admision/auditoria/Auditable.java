package com.hospital.admision.auditoria;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un método de servicio para que su ejecución quede registrada
 * automáticamente en la tabla de auditoría (ver AuditoriaAspect).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    // REGISTRO, MODIFICACION, ELIMINACION, CAMBIO_ESTADO
    String operacion();

    // Nombre de la entidad afectada
    String entidad();
}
