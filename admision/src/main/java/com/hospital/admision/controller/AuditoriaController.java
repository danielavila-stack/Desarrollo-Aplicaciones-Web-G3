package com.hospital.admision.controller;

import com.hospital.admision.model.Auditoria;
import com.hospital.admision.repository.AuditoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // GET: http://localhost:8081/api/auditoria  (opcional ?entidad=Paciente)
    @GetMapping
    public ResponseEntity<List<Auditoria>> listar(@RequestParam(required = false) String entidad) {
        if (entidad != null && !entidad.isBlank()) {
            return ResponseEntity.ok(auditoriaRepository.findByEntidadOrderByFechaHoraDesc(entidad));
        }
        return ResponseEntity.ok(auditoriaRepository.findAllByOrderByFechaHoraDesc());
    }
}
