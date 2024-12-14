package com.grupo2backend.entity;

import java.util.Map;

public class PedidoEntity {
    private Long id_pedido;
    private Long id_zona;
    private Long id_repartidor;
    private Map<String, Object> coordenada_direccion;
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

    public Long getId_repartidor() {
        return id_repartidor;
    }

    public void setId_repartidor(Long id_repartidor) {
        this.id_repartidor = id_repartidor;
    }

    public Map<String, Object> getCoordenada_direccion() {
        return coordenada_direccion;
    }

    public void setCoordenada_direccion(Map<String, Object> coordenada_direccion) {
        this.coordenada_direccion = coordenada_direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}


