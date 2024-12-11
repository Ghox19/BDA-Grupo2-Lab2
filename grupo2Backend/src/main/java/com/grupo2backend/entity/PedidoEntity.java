package com.grupo2backend.entity;

import java.awt.*;

public class PedidoEntity {
    private Long id_pedido;
    private Long id_zona;
    private Long id_orden;
    private Long id_repartidor;
    private Point coordenadaDireccion;
    private String estado;

    public Long getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(Long id_pedido) {
        this.id_pedido = id_pedido;
    }

    public Long getId_zona() {
        return id_zona;
    }

    public void setId_zona(Long id_zona) {
        this.id_zona = id_zona;
    }

    public Long getId_orden() {
        return id_orden;
    }

    public void setId_orden(Long id_orden) {
        this.id_orden = id_orden;
    }

    public Long getId_repartidor() {
        return id_repartidor;
    }

    public void setId_repartidor(Long id_repartidor) {
        this.id_repartidor = id_repartidor;
    }

    public Point getCoordenadaDireccion() {
        return coordenadaDireccion;
    }

    public void setCoordenadaDireccion(Point coordenadaDireccion) {
        this.coordenadaDireccion = coordenadaDireccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
