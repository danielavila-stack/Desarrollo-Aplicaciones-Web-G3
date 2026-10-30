package com.hospital.admision.service;

import com.hospital.admision.model.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {

    List<UsuarioDTO> listarUsuarios();

    UsuarioDTO obtenerPorId(Long id);

    UsuarioDTO obtenerPorUsername(String username);

    UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO);

    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO);

    UsuarioDTO cambiarEstado(Long id, Boolean activo);
}
