package com.hospital.admision.repository;

import com.hospital.admision.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Bitácora completa, de la operación más reciente a la más antigua
    List<Auditoria> findAllByOrderByFechaHoraDesc();

    List<Auditoria> findByEntidadOrderByFechaHoraDesc(String entidad);
}
