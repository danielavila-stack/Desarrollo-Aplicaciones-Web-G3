package com.hospital.admision.controller;

import com.hospital.admision.model.dto.RolDTO;
import com.hospital.admision.service.RolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    // GET: http://localhost:8081/api/roles
    @GetMapping
    public ResponseEntity<List<RolDTO>> listar() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    // GET: http://localhost:8081/api/roles/1
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerPorId(id));
    }

    // POST: http://localhost:8081/api/roles
    @PostMapping
    public ResponseEntity<RolDTO> registrar(@RequestBody RolDTO rolDTO) {
        return new ResponseEntity<>(rolService.registrarRol(rolDTO), HttpStatus.CREATED);
    }

    // PUT: http://localhost:8081/api/roles/1
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizar(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        return ResponseEntity.ok(rolService.actualizarRol(id, rolDTO));
    }

    // PATCH: http://localhost:8081/api/roles/1/estado?activo=false
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RolDTO> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        return ResponseEntity.ok(rolService.cambiarEstado(id, activo));
    }
}
