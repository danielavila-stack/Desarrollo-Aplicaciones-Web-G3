package com.hospital.admision.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AdmisionDTO {

    private Long id;
    private String codigoAdmision;
    private Long pacienteId;
    private String tipoAtencion; // Ambulatoria, Emergencia, Hospitalizacion, Referencia
    private String servicio;
    private String especialidad;
    private String medicoResponsable;
    private String motivo;
    private String prioridad;
    private String estado;
    private String usuarioRegistro;
    private AcompananteDTO acompanante;
    private LocalDate fechaIngreso;
    private LocalTime horaIngreso;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;

    public AdmisionDTO() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoAdmision() { return codigoAdmision; }
    public void setCodigoAdmision(String codigoAdmision) { this.codigoAdmision = codigoAdmision; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getTipoAtencion() { return tipoAtencion; }
    public void setTipoAtencion(String tipoAtencion) { this.tipoAtencion = tipoAtencion; }
    public String getServicio() { return servicio; }
    public void setServicio(String servicio) { this.servicio = servicio; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public String getMedicoResponsable() { return medicoResponsable; }
    public void setMedicoResponsable(String medicoResponsable) { this.medicoResponsable = medicoResponsable; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(String usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
    public AcompananteDTO getAcompanante() { return acompanante; }
    public void setAcompanante(AcompananteDTO acompanante) { this.acompanante = acompanante; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalTime getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(LocalTime horaIngreso) { this.horaIngreso = horaIngreso; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }
}