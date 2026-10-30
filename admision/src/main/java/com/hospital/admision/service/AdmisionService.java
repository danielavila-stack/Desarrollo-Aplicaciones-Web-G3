package com.hospital.admision.service;

import com.hospital.admision.model.dto.AdmisionDTO;
import com.hospital.admision.model.dto.AltaDTO;
import com.hospital.admision.model.dto.TriajeDTO;

import java.util.List;

public interface AdmisionService {

    AdmisionDTO registrarAdmision(AdmisionDTO admisionDTO);

    TriajeDTO registrarTriaje(TriajeDTO triajeDTO);

    AdmisionDTO procesarAlta(AltaDTO altaDTO);

    List<AdmisionDTO> obtenerPacientesEnEspera();

    AdmisionDTO buscarPorCodigo(String codigo);

    List<AdmisionDTO> listarPorPaciente(Long pacienteId);

    void eliminarAdmision(Long id);
}