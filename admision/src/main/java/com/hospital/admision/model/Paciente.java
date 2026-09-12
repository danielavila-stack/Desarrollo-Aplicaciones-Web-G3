package com.hospital.admision.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "es_no_identificado", columnDefinition = "TINYINT(1)")
    private Boolean esNoIdentificado;


    // Relación 1:N -> Un paciente puede tener múltiples admisiones
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Admision> admisiones;

    public Paciente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public Boolean getEsNoIdentificado() { return esNoIdentificado; }
    public void setEsNoIdentificado(Boolean esNoIdentificado) { this.esNoIdentificado = esNoIdentificado; }
    public List<Admision> getAdmisiones() { return admisiones; }
    public void setAdmisiones(List<Admision> admisiones) { this.admisiones = admisiones; }
}