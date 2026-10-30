package com.hospital.admision.service;

import com.hospital.admision.model.dto.RolDTO;

import java.util.List;

public interface RolService {

    List<RolDTO> listarRoles();

    RolDTO obtenerPorId(Long id);

    RolDTO registrarRol(RolDTO rolDTO);

    RolDTO actualizarRol(Long id, RolDTO rolDTO);

    RolDTO cambiarEstado(Long id, Boolean activo);
}
