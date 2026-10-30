package com.hospital.admision.service.impl;

import com.hospital.admision.auditoria.Auditable;
import com.hospital.admision.model.Acompanante;
import com.hospital.admision.model.Admision;
import com.hospital.admision.model.Paciente;
import com.hospital.admision.model.Triaje;
import com.hospital.admision.model.dto.AcompananteDTO;
import com.hospital.admision.model.dto.AdmisionDTO;
import com.hospital.admision.model.dto.AltaDTO;
import com.hospital.admision.model.dto.TriajeDTO;
import com.hospital.admision.repository.AdmisionRepository;
import com.hospital.admision.repository.PacienteRepository;
import com.hospital.admision.repository.TriajeRepository;
import com.hospital.admision.service.AdmisionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdmisionServiceImpl implements AdmisionService {

    // Inyección de Dependencias (DI) mediante constructor
    private final AdmisionRepository admisionRepository;
    private final PacienteRepository pacienteRepository;
    private final TriajeRepository triajeRepository;

    public AdmisionServiceImpl(AdmisionRepository admisionRepository,
                               PacienteRepository pacienteRepository,
                               TriajeRepository triajeRepository) {
        this.admisionRepository = admisionRepository;
        this.pacienteRepository = pacienteRepository;
        this.triajeRepository = triajeRepository;
    }

    @Override
    @Transactional
    @Auditable(operacion = "REGISTRO", entidad = "Admision")
    public AdmisionDTO registrarAdmision(AdmisionDTO admisionDTO) {
        // 1. Validar y obtener el paciente
        Paciente paciente = pacienteRepository.findById(admisionDTO.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + admisionDTO.getPacienteId()));

        // 2. Mapear DTO a Entidad Admision
        Admision admision = new Admision();
        admision.setCodigoAdmision("ADM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        admision.setFechaIngreso(LocalDate.now());
        admision.setHoraIngreso(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        admision.setTipoAtencion(admisionDTO.getTipoAtencion());
        admision.setServicio(admisionDTO.getServicio());
        admision.setEspecialidad(admisionDTO.getEspecialidad());
        admision.setMedicoResponsable(admisionDTO.getMedicoResponsable());
        admision.setMotivo(admisionDTO.getMotivo());
        admision.setPrioridad("PENDIENTE");
        admision.setEstado("ABIERTA");
        // El usuario de registro es el que inició sesión (Spring Security)
        admision.setUsuarioRegistro(SecurityContextHolder.getContext().getAuthentication().getName());
        admision.setPaciente(paciente);

        // 3. Procesar datos del acompañante si existen
        if (admisionDTO.getAcompanante() != null) {
            AcompananteDTO acompDTO = admisionDTO.getAcompanante();
            Acompanante acompanante = new Acompanante();
            acompanante.setNombreCompleto(acompDTO.getNombreCompleto());
            acompanante.setDocumento(acompDTO.getDocumento());
            acompanante.setParentesco(acompDTO.getParentesco());
            acompanante.setTelefono(acompDTO.getTelefono());
            admision.setAcompanante(acompanante);
        }

        // 4. Guardar entidad
        Admision guardada = admisionRepository.save(admision);
        return mapearAAdmisionDTO(guardada);
    }

    @Override
    @Transactional
    @Auditable(operacion = "REGISTRO", entidad = "Triaje")
    public TriajeDTO registrarTriaje(TriajeDTO triajeDTO) {
        // 1. Obtener la admisión correspondiente
        Admision admision = admisionRepository.findById(triajeDTO.getAdmisionId())
                .orElseThrow(() -> new RuntimeException("Admisión no encontrada con ID: " + triajeDTO.getAdmisionId()));

        // Relación 1:1 -> una admisión solo puede tener un triaje
        if (triajeRepository.findByAdmisionId(admision.getId()).isPresent()) {
            throw new RuntimeException("La admisión " + admision.getCodigoAdmision() + " ya tiene un triaje registrado.");
        }

        // 2. Crear entidad Triaje
        Triaje triaje = new Triaje();
        triaje.setFechaHora(LocalDateTime.now());
        triaje.setPresionArterial(triajeDTO.getPresionArterial());
        triaje.setFrecuenciaCardiaca(triajeDTO.getFrecuenciaCardiaca());
        triaje.setTemperatura(triajeDTO.getTemperatura());
        triaje.setSaturacion(triajeDTO.getSaturacion());
        triaje.setPrioridad(triajeDTO.getPrioridad());
        triaje.setObservaciones(triajeDTO.getObservaciones());
        triaje.setEnfermeroResponsable(triajeDTO.getEnfermeroResponsable());
        triaje.setAdmision(admision);

        // 3. Actualizar la prioridad en la Admisión
        admision.setPrioridad(triajeDTO.getPrioridad());
        admisionRepository.save(admision);

        // 4. Guardar Triaje
        Triaje triajeGuardado = triajeRepository.save(triaje);

        // 5. Retornar DTO con el ID generado
        triajeDTO.setId(triajeGuardado.getId());
        triajeDTO.setFechaHora(triajeGuardado.getFechaHora());
        return triajeDTO;
    }

    @Override
    @Transactional
    @Auditable(operacion = "MODIFICACION", entidad = "Admision")
    public AdmisionDTO procesarAlta(AltaDTO altaDTO) {
        Admision admision = admisionRepository.findById(altaDTO.getAdmisionId())
                .orElseThrow(() -> new RuntimeException("Admisión no encontrada con ID: " + altaDTO.getAdmisionId()));

        // 1. Actualizar el estado a CERRADA
        admision.setEstado("CERRADA");

        // 2. REGISTRAR LA FECHA Y HORA ACTUAL DE SALIDA
        admision.setFechaSalida(LocalDate.now());
        admision.setHoraSalida(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        // 3. Guardar cambios en la base de datos
        Admision actualizada = admisionRepository.save(admision);

        return mapearAAdmisionDTO(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmisionDTO> obtenerPacientesEnEspera() {
        return admisionRepository.findByTipoAtencionAndEstado("Emergencia", "ABIERTA")
                .stream()
                .map(this::mapearAAdmisionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdmisionDTO buscarPorCodigo(String codigo) {
        Admision admision = admisionRepository.findByCodigoAdmision(codigo)
                .orElseThrow(() -> new RuntimeException("Admisión no encontrada con código: " + codigo));
        return mapearAAdmisionDTO(admision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdmisionDTO> listarPorPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new RuntimeException("Paciente no encontrado con ID: " + pacienteId);
        }
        return admisionRepository.findByPacienteIdOrderByFechaIngresoDesc(pacienteId)
                .stream()
                .map(this::mapearAAdmisionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Auditable(operacion = "ELIMINACION", entidad = "Admision")
    public void eliminarAdmision(Long id) {
        Admision admision = admisionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admisión no encontrada con ID: " + id));

        // Se quita de la lista del paciente; CascadeType.ALL elimina su acompañante y triaje
        admision.getPaciente().getAdmisiones().remove(admision);
        admisionRepository.delete(admision);
    }

    // Método auxiliar para transformar Entidad -> DTO
    private AdmisionDTO mapearAAdmisionDTO(Admision admision) {
        AdmisionDTO dto = new AdmisionDTO();
        dto.setId(admision.getId());
        dto.setCodigoAdmision(admision.getCodigoAdmision());
        dto.setPacienteId(admision.getPaciente().getId());
        dto.setPacienteNombre(admision.getPaciente().getNombres() + " " + admision.getPaciente().getApellidos());
        dto.setTipoAtencion(admision.getTipoAtencion());
        dto.setServicio(admision.getServicio());
        dto.setEspecialidad(admision.getEspecialidad());
        dto.setMedicoResponsable(admision.getMedicoResponsable());
        dto.setMotivo(admision.getMotivo());
        dto.setPrioridad(admision.getPrioridad());
        dto.setEstado(admision.getEstado());
        dto.setUsuarioRegistro(admision.getUsuarioRegistro());
        dto.setFechaIngreso(admision.getFechaIngreso());
        dto.setHoraIngreso(admision.getHoraIngreso());

        // MAPEAR FECHA Y HORA DE SALIDA AL DTO
        dto.setFechaSalida(admision.getFechaSalida());
        dto.setHoraSalida(admision.getHoraSalida());

        return dto;
    }
}