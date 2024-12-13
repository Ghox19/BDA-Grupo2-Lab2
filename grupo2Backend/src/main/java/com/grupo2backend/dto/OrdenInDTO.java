package com.grupo2backend.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrdenInDTO {
    private Long id_orden;
    private Timestamp fecha_orden;
    private String estado;
    private Long id_cliente;
    private BigDecimal total;

    public OrdenInDTO(Long id_orden, Timestamp fecha_orden, String estado, Long id_cliente, BigDecimal total) {
        this.id_orden = id_orden;
        this.fecha_orden = fecha_orden;
        this.estado = estado;
        this.id_cliente = id_cliente;
        this.total = total;
    }

    public Long getId_orden() {
        return id_orden;
    }

    public void setId_orden(Long id_orden) {
        this.id_orden = id_orden;
    }

    public Timestamp getFecha_orden() {
        return fecha_orden;
    }

    public void setFecha_orden(Timestamp fecha_orden) {
        this.fecha_orden = fecha_orden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
