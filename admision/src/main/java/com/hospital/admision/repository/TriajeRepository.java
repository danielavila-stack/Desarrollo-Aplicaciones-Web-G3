package com.hospital.admision.repository;

import com.hospital.admision.model.Triaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TriajeRepository extends JpaRepository<Triaje, Long> {

    // Buscar el triaje de una admisión específica
    Optional<Triaje> findByAdmisionId(Long admisionId);
}