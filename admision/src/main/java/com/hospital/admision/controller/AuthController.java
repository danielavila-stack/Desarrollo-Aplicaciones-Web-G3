package com.hospital.admision.controller;

import com.hospital.admision.model.dto.UsuarioDTO;
import com.hospital.admision.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Datos del usuario que inició sesión (el frontend lo usa para mostrar solo las opciones de su rol)
    // GET: http://localhost:8081/api/auth/me
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> usuarioActual(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerPorUsername(authentication.getName()));
    }
}
