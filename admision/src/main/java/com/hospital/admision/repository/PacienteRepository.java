package com.hospital.admision.repository;

import com.hospital.admision.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Buscar paciente por DNI
    Optional<Paciente> findByDni(String dni);

    // Verificar si existe un paciente por DNI
    boolean existsByDni(String dni);
}
