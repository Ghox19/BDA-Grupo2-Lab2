package com.grupo2backend.entity;

import java.util.List;

public class RepartidorEntity {

    private Long id_repartidor;
    private String nombre;
    private List<Long> pedidos;

    public Long getId_repartidor() {
        return id_repartidor;
    }

    public void setId_repartidor(Long id_repartidor) {
        this.id_repartidor = id_repartidor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Long> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Long> pedidos) {
        this.pedidos = pedidos;
    }
}
