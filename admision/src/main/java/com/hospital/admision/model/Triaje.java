package com.hospital.admision.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "triajes")
public class Triaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "presion_arterial", length = 20)
    private String presionArterial;

    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    private Double temperatura;

    private Integer saturacion;

    @Column(nullable = false, length = 30)
    private String prioridad; // CRÍTICA, URGENTE, MODERADA, LEVE

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "enfermero_responsable", length = 100)
    private String enfermeroResponsable;

    // Relación 1:1 -> Un triaje pertenece exactamente a una admisión
    @OneToOne
    @JoinColumn(name = "admision_id", nullable = false, unique = true)
    private Admision admision;

    public Triaje() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Admision getAdmision() { return admision; }
    public void setAdmision(Admision admision) { this.admision = admision; }
}