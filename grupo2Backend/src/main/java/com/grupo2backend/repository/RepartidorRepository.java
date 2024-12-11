package com.grupo2backend.repository;

import com.grupo2backend.entity.RepartidorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

@Repository
public class RepartidorRepository {

    @Autowired
    private Sql2o sql2o;

    public List<RepartidorEntity> findAll() {
        String sql = "SELECT id_repartidor, nombre FROM repartidor";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(RepartidorEntity.class);
        }
    }

    public void save(RepartidorEntity entity) {
        String sql = "INSERT INTO repartidor (nombre) VALUES (:nombre)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("nombre", entity.getNombre())
                    .executeUpdate();
        }
    }

    public RepartidorEntity findById(Long id) {
        String sql = "SELECT id_repartidor, nombre FROM repartidor WHERE id_repartidor = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(RepartidorEntity.class);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM repartidor WHERE id_repartidor = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }

    public String actualizarEstadoOrden(Integer id) {
        String sql = "SELECT actualizar_estado_orden(:id)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeScalar(String.class);
        }
    }

    public void updateCliente(Long id, RepartidorEntity repartidorEntity) {
        final String updateQuery =
                "UPDATE repartidor SET nombre = :nombre WHERE id_repartidor = :id_repartidor";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(updateQuery)
                    .addParameter("id_cliente", id)
                    .addParameter("nombre", repartidorEntity.getNombre())
                    .executeUpdate();
            con.commit();
        }
    }
}
