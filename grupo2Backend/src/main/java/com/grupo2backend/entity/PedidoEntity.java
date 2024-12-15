package com.grupo2backend.entity;

import java.util.Map;

public class PedidoEntity {
    private Long id_pedido;
    private Long id_zona;
    private Long id_cliente;
    private Map<String, Object> coordenada_direccion;
    private String direccion;
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

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Map<String, Object> getCoordenada_direccion() {
        return coordenada_direccion;
    }

    public void setCoordenada_direccion(Map<String, Object> coordenada_direccion) {
        this.coordenada_direccion = coordenada_direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}


