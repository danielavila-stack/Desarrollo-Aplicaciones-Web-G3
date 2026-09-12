package com.hospital.admision.model.dto;

import java.time.LocalDateTime;

public class AltaDTO {

    private Long admisionId;
    private LocalDateTime fechaHoraSalida;
    private String resultado; // Alta médica, Hospitalización, Referencia, Traslado, Fallecimiento
    private String medicoResponsable;
    private String diagnostico;
    private String indicaciones;
    private String tratamiento;
    private String recomendaciones;

    public AltaDTO() {}

    // Getters y Setters
    public Long getAdmisionId() { return admisionId; }
    public void setAdmisionId(Long admisionId) { this.admisionId = admisionId; }
    public LocalDateTime getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public String getMedicoResponsable() { return medicoResponsable; }
    public void setMedicoResponsable(String medicoResponsable) { this.medicoResponsable = medicoResponsable; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(String recomendaciones) { this.recomendaciones = recomendaciones; }
}