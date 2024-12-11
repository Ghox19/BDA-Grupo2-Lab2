package com.grupo2backend.entity;


import java.awt.*;
import java.util.List;

public class ComunasSantiagoEntity {
    private Long id;
    private Integer cod_comuna;
    private String comuna;
    private String provincia;
    private String region;
    private Polygon geom;
    private String pago;
    private List<Long> pedidos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCod_comuna() {
        return cod_comuna;
    }

    public void setCod_comuna(Integer cod_comuna) {
        this.cod_comuna = cod_comuna;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Polygon getGeom() {
        return geom;
    }

    public void setGeom(Polygon geom) {
        this.geom = geom;
    }

    public String getPago() {
        return pago;
    }

    public void setPago(String pago) {
        this.pago = pago;
    }

    public List<Long> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Long> pedidos) {
        this.pedidos = pedidos;
    }
}
