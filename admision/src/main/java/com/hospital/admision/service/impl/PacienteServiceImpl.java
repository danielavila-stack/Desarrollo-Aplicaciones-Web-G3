package com.hospital.admision.service.impl;

import com.hospital.admision.auditoria.Auditable;
import com.hospital.admision.model.Paciente;
import com.hospital.admision.repository.PacienteRepository;
import com.hospital.admision.service.PacienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional
    @Auditable(operacion = "REGISTRO", entidad = "Paciente")
    public Paciente registrarPaciente(Paciente paciente) {
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new RuntimeException("Ya existe un paciente con el DNI: " + paciente.getDni());
        }
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    @Auditable(operacion = "MODIFICACION", entidad = "Paciente")
    public Paciente actualizarPaciente(Long id, Paciente datos) {
        Paciente paciente = obtenerPorId(id);

        // El DNI no puede repetirse con otro paciente
        pacienteRepository.findByDni(datos.getDni())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> { throw new RuntimeException("Ya existe un paciente con el DNI: " + datos.getDni()); });

        paciente.setDni(datos.getDni());
        paciente.setNombres(datos.getNombres());
        paciente.setApellidos(datos.getApellidos());
        paciente.setEsNoIdentificado(datos.getEsNoIdentificado());
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    @Auditable(operacion = "ELIMINACION", entidad = "Paciente")
    public void eliminarPaciente(Long id) {
        Paciente paciente = obtenerPorId(id);

        // Integridad: no se elimina un paciente que sigue internado (admisión ABIERTA)
        boolean tieneAdmisionAbierta = paciente.getAdmisiones().stream()
                .anyMatch(a -> "ABIERTA".equals(a.getEstado()));
        if (tieneAdmisionAbierta) {
            throw new RuntimeException("No se puede eliminar: el paciente tiene una admisión ABIERTA.");
        }

        // CascadeType.ALL elimina también sus admisiones, acompañantes y triajes
        pacienteRepository.delete(paciente);
    }
}
