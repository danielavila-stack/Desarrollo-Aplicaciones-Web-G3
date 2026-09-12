package com.hospital.admision.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "admisiones")
public class Admision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_admision", nullable = false, unique = true, length = 30)
    private String codigoAdmision;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "hora_ingreso", nullable = false)
    private LocalTime horaIngreso;

    @Column(name = "tipo_atencion", nullable = false, length = 50)
    private String tipoAtencion;

    @Column(length = 100)
    private String servicio;

    @Column(length = 100)
    private String especialidad;

    @Column(name = "medico_responsable", length = 100)
    private String medicoResponsable;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 30)
    private String prioridad;

    @Column(nullable = false, length = 20)
    private String estado; // ABIERTA, CERRADA

    @Column(name = "usuario_registro", length = 50)
    private String usuarioRegistro;

    // N:1 con Paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // 1:1 con Acompañante
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "acompanante_id", referencedColumnName = "id")
    private Acompanante acompanante;

    // 1:1 con Triaje
    @OneToOne(mappedBy = "admision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Triaje triaje;

    public Admision() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoAdmision() { return codigoAdmision; }
    public void setCodigoAdmision(String codigoAdmision) { this.codigoAdmision = codigoAdmision; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalTime getHoraIngreso() { return horaIngreso; }
    public void setHoraIngreso(LocalTime horaIngreso) { this.horaIngreso = horaIngreso; }

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

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Acompanante getAcompanante() { return acompanante; }
    public void setAcompanante(Acompanante acompanante) { this.acompanante = acompanante; }

    public Triaje getTriaje() { return triaje; }
    public void setTriaje(Triaje triaje) { this.triaje = triaje; }
}