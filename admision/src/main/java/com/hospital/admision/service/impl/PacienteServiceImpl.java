package com.hospital.admision.service.impl;

import com.hospital.admision.model.Paciente;
import com.hospital.admision.repository.PacienteRepository;
import com.hospital.admision.service.PacienteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Paciente registrarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }
}