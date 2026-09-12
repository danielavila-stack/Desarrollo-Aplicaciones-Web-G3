package com.hospital.admision.model.dto;

public class AcompananteDTO {

    private String nombreCompleto;
    private String documento;
    private String parentesco;
    private String telefono;

    public AcompananteDTO() {}

    // Getters y Setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}