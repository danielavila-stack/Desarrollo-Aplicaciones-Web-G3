package com.hospital.admision.service;

import com.hospital.admision.model.Paciente;
import java.util.List;

public interface PacienteService {
    Paciente registrarPaciente(Paciente paciente);
    List<Paciente> obtenerTodos();
}