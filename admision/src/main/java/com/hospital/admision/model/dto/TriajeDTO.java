package com.hospital.admision.model.dto;

import java.time.LocalDateTime;

public class TriajeDTO {

    private Long id;
    private Long admisionId;
    private LocalDateTime fechaHora;
    private String presionArterial;
    private Integer frecuenciaCardiaca;
    private Double temperatura;
    private Integer saturacion;
    private String prioridad; // PRIORIDAD 1 - CRITICA, PRIORIDAD 2 - URGENTE, etc.
    private String observaciones;
    private String enfermeroResponsable;

    public TriajeDTO() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAdmisionId() { return admisionId; }
    public void setAdmisionId(Long admisionId) { this.admisionId = admisionId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getPresionArterial() { return presionArterial; }
    public void setPresionArterial(String presionArterial) { this.presionArterial = presionArterial; }
    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }
    public Integer getSaturacion() { return saturacion; }
    public void setSaturacion(Integer saturacion) { this.saturacion = saturacion; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public String getEnfermeroResponsable() { return enfermeroResponsable; }
    public void setEnfermeroResponsable(String enfermeroResponsable) { this.enfermeroResponsable = enfermeroResponsable; }
}