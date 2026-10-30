package com.hospital.admision.service.impl;

import com.hospital.admision.auditoria.Auditable;
import com.hospital.admision.model.Rol;
import com.hospital.admision.model.Usuario;
import com.hospital.admision.model.dto.UsuarioDTO;
import com.hospital.admision.repository.RolRepository;
import com.hospital.admision.repository.UsuarioRepository;
import com.hospital.admision.service.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        return mapearAUsuarioDTO(buscarUsuario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return mapearAUsuarioDTO(usuario);
    }

    @Override
    @Transactional
    @Auditable(operacion = "REGISTRO", entidad = "Usuario")
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getUsername() == null || usuarioDTO.getUsername().isBlank()) {
            throw new RuntimeException("El nombre de usuario es obligatorio.");
        }
        if (usuarioRepository.existsByUsername(usuarioDTO.getUsername().trim())) {
            throw new RuntimeException("El nombre de usuario ya está registrado: " + usuarioDTO.getUsername());
        }
        if (usuarioDTO.getPassword() == null || usuarioDTO.getPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioDTO.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        usuario.setRol(buscarRol(usuarioDTO.getRolId()));

        return mapearAUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    @Auditable(operacion = "MODIFICACION", entidad = "Usuario")
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = buscarUsuario(id);

        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setRol(buscarRol(usuarioDTO.getRolId()));

        // La contraseña solo se cambia si se envía una nueva
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isBlank()) {
            if (usuarioDTO.getPassword().length() < 6) {
                throw new RuntimeException("La contraseña debe tener al menos 6 caracteres.");
            }
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        return mapearAUsuarioDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    @Auditable(operacion = "CAMBIO_ESTADO", entidad = "Usuario")
    public UsuarioDTO cambiarEstado(Long id, Boolean activo) {
        Usuario usuario = buscarUsuario(id);

        // Evita que el administrador se bloquee a sí mismo
        String usuarioActual = SecurityContextHolder.getContext().getAuthentication().getName();
        if (usuario.getUsername().equals(usuarioActual) && !activo) {
            throw new RuntimeException("No puede desactivar su propio usuario.");
        }

        usuario.setActivo(activo);
        return mapearAUsuarioDTO(usuarioRepository.save(usuario));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    private Rol buscarRol(Long rolId) {
        if (rolId == null) {
            throw new RuntimeException("Debe seleccionar un rol.");
        }
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + rolId));
        if (!rol.getActivo()) {
            throw new RuntimeException("No se puede asignar el rol " + rol.getNombre() + " porque está inactivo.");
        }
        return rol;
    }

    private UsuarioDTO mapearAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUsername(usuario.getUsername());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        return dto;
    }
}
