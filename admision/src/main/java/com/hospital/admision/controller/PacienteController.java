package com.hospital.admision.controller;

import com.hospital.admision.model.Paciente;
import com.hospital.admision.model.dto.AdmisionDTO;
import com.hospital.admision.service.AdmisionService;
import com.hospital.admision.service.PacienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final AdmisionService admisionService;

    public PacienteController(PacienteService pacienteService, AdmisionService admisionService) {
        this.pacienteService = pacienteService;
        this.admisionService = admisionService;
    }

    @PostMapping
    public ResponseEntity<Paciente> registrarPaciente(@RequestBody Paciente paciente) {
        Paciente nuevoPaciente = pacienteService.registrarPaciente(paciente);
        return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    // Relación 1:N -> admisiones registradas para un paciente
    // GET: http://localhost:8081/api/pacientes/1/admisiones
    @GetMapping("/{id}/admisiones")
    public ResponseEntity<List<AdmisionDTO>> obtenerAdmisiones(@PathVariable Long id) {
        return ResponseEntity.ok(admisionService.listarPorPaciente(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.actualizarPaciente(id, paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        pacienteService.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}
