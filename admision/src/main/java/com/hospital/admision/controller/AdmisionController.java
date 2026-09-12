package com.hospital.admision.controller;

import com.hospital.admision.model.dto.AdmisionDTO;
import com.hospital.admision.model.dto.AltaDTO;
import com.hospital.admision.model.dto.TriajeDTO;
import com.hospital.admision.service.AdmisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admisiones")
public class AdmisionController {

    // Inyección de la interfaz de servicio por constructor
    private final AdmisionService admisionService;

    public AdmisionController(AdmisionService admisionService) {
        this.admisionService = admisionService;
    }

    // 1. Registrar una nueva admisión (Ambulatoria, Emergencia, Hospitalización)
    // POST: http://localhost:8080/api/admisiones
    @PostMapping
    public ResponseEntity<AdmisionDTO> registrarAdmision(@RequestBody AdmisionDTO admisionDTO) {
        AdmisionDTO nuevaAdmision = admisionService.registrarAdmision(admisionDTO);
        return new ResponseEntity<>(nuevaAdmision, HttpStatus.CREATED);
    }

    // 2. Registrar el triaje de una admisión de emergencia
    // POST: http://localhost:8080/api/admisiones/triaje
    @PostMapping("/triaje")
    public ResponseEntity<TriajeDTO> registrarTriaje(@RequestBody TriajeDTO triajeDTO) {
        TriajeDTO triajeRegistrado = admisionService.registrarTriaje(triajeDTO);
        return new ResponseEntity<>(triajeRegistrado, HttpStatus.CREATED);
    }

    // 3. Registrar el alta médica o salida del paciente
    // POST: http://localhost:8080/api/admisiones/alta
    @PostMapping("/alta")
    public ResponseEntity<AdmisionDTO> procesarAlta(@RequestBody AltaDTO altaDTO) {
        AdmisionDTO admisionActualizada = admisionService.procesarAlta(altaDTO);
        return ResponseEntity.ok(admisionActualizada);
    }

    // 4. Obtener la sala de espera de emergencia (Pacientes activos/abiertos)
    // GET: http://localhost:8080/api/admisiones/sala-espera
    @GetMapping("/sala-espera")
    public ResponseEntity<List<AdmisionDTO>> obtenerSalaEspera() {
        List<AdmisionDTO> pacientesEspera = admisionService.obtenerPacientesEnEspera();
        return ResponseEntity.ok(pacientesEspera);
    }

    // 5. Buscar una admisión específica por su código (Ej: ADM-A1B2C3)
    // GET: http://localhost:8080/api/admisiones/codigo/ADM-A1B2C3
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<AdmisionDTO> buscarPorCodigo(@PathVariable String codigo) {
        AdmisionDTO admision = admisionService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(admision);
    }
}