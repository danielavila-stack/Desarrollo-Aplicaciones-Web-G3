package com.hospital.admision.service.impl;

import com.hospital.admision.auditoria.Auditable;
import com.hospital.admision.model.Rol;
import com.hospital.admision.model.dto.RolDTO;
import com.hospital.admision.repository.RolRepository;
import com.hospital.admision.repository.UsuarioRepository;
import com.hospital.admision.service.RolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public RolServiceImpl(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll()
                .stream()
                .map(this::mapearARolDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Long id) {
        return mapearARolDTO(buscarRol(id));
    }

    @Override
    @Transactional
    @Auditable(operacion = "REGISTRO", entidad = "Rol")
    public RolDTO registrarRol(RolDTO rolDTO) {
        String nombre = normalizarNombre(rolDTO.getNombre());
        if (rolRepository.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + nombre);
        }
        Rol rol = new Rol(nombre, rolDTO.getDescripcion());
        return mapearARolDTO(rolRepository.save(rol));
    }

    @Override
    @Transactional
    @Auditable(operacion = "MODIFICACION", entidad = "Rol")
    public RolDTO actualizarRol(Long id, RolDTO rolDTO) {
        Rol rol = buscarRol(id);
        String nombre = normalizarNombre(rolDTO.getNombre());

        // Validar que el nuevo nombre no esté usado por otro rol
        rolRepository.findByNombre(nombre)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> { throw new RuntimeException("Ya existe un rol con el nombre: " + nombre); });

        rol.setNombre(nombre);
        rol.setDescripcion(rolDTO.getDescripcion());
        return mapearARolDTO(rolRepository.save(rol));
    }

    @Override
    @Transactional
    @Auditable(operacion = "CAMBIO_ESTADO", entidad = "Rol")
    public RolDTO cambiarEstado(Long id, Boolean activo) {
        Rol rol = buscarRol(id);
        if ("ADMINISTRADOR".equals(rol.getNombre()) && !activo) {
            throw new RuntimeException("No se puede desactivar el rol ADMINISTRADOR.");
        }
        rol.setActivo(activo);
        return mapearARolDTO(rolRepository.save(rol));
    }

    private Rol buscarRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }

    // Los nombres de rol se guardan en mayúsculas y sin espacios (ej. "Médico" -> "MEDICO")
    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre del rol es obligatorio.");
        }
        return java.text.Normalizer.normalize(nombre.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace(' ', '_')
                .toUpperCase();
    }

    private RolDTO mapearARolDTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        dto.setActivo(rol.getActivo());
        dto.setTotalUsuarios(usuarioRepository.countByRolId(rol.getId()));
        return dto;
    }
}
