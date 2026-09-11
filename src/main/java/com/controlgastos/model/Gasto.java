package com.controlgastos.model;

import java.time.LocalDateTime;

public class Gasto {

    private Long id;
    private String descripcion;
    private Double monto;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fecha;
    private String categoria;

    // Constructor vacío
    public Gasto() {
    }

    // Constructor completo
    public Gasto(Long id, String descripcion, Double monto,
                 Double latitud, Double longitud,
                 LocalDateTime fecha, String categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}