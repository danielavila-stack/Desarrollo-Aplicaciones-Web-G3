package com.hospital.admision.repository;

import com.hospital.admision.model.Admision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmisionRepository extends JpaRepository<Admision, Long> {

    // Buscar admisión por su código único (ej. ADM-000125)
    Optional<Admision> findByCodigoAdmision(String codigoAdmision);

    // Listar admisiones activas por tipo de atención y estado (ej. Emergencia, ABIERTA)
    List<Admision> findByTipoAtencionAndEstado(String tipoAtencion, String estado);

    // Listar todas las admisiones en un estado específico
    List<Admision> findByEstado(String estado);

    // Relación N:1 -> todas las admisiones de un paciente (usa la FK paciente_id)
    List<Admision> findByPacienteIdOrderByFechaIngresoDesc(Long pacienteId);
}
