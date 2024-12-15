package com.grupo2backend.repository;

import com.grupo2backend.dto.OrdenInDTO;
import com.grupo2backend.entity.OrdenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class OrdenRepository {

    @Autowired
    private Sql2o sql2o;

    public List<OrdenEntity> findAll() {
        String sql = "SELECT id_orden, fecha_orden, estado, id_pedido, id_cliente, total FROM orden";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql).executeAndFetch(OrdenEntity.class);
        }
    }

    public Long save(OrdenInDTO ordenDTO) {
        // Primero crear el pedido con coordenadas 0,0

        try (Connection con = sql2o.beginTransaction()) {
            String sqlPedido = "INSERT INTO pedido (id_zona, id_cliente,coordenada_direccion, direccion, estado) " +
                    "VALUES (1, null, ST_GeomFromText('POINT(0 0)', 4326), ' ', 'pendiente') RETURNING id_pedido";
            // Obtener el id del pedido creado

            Integer idPedido = (Integer) con.createQuery(sqlPedido, true)
                    .addParameter("id_cliente", ordenDTO.getId_cliente())
                    .addParameter("estado", "en_proceso")
                    .executeUpdate()
                    .getKey();

            // Crear la orden usando el DTO
            String sqlOrden = "INSERT INTO orden (fecha_orden, estado, id_pedido, id_cliente, total) " +
                    "VALUES (:fecha_orden, :estado, :id_pedido, :id_cliente, :total)";

            Long idOrden = (Long) con.createQuery(sqlOrden, true)
                    .addParameter("fecha_orden", ordenDTO.getFecha_orden())
                    .addParameter("estado", ordenDTO.getEstado())
                    .addParameter("id_cliente", ordenDTO.getId_cliente())
                    .addParameter("id_pedido", idPedido)
                    .addParameter("total", ordenDTO.getTotal())
                    .executeUpdate()
                    .getKey(Long.class);

            con.commit();
            return idOrden;
        }
    }


    public List<OrdenEntity> findByClienteId(Long idCliente) {
        String sql = "SELECT * FROM orden WHERE id_cliente = :idCliente";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idCliente", idCliente)
                    .executeAndFetch(OrdenEntity.class);
        }
    }

    public Optional<Integer> findByEstadoAndIdCliente(Long idCliente){
        String sql = "SELECT id_orden FROM orden WHERE id_cliente = :idCliente AND estado = :estado";
        try(Connection con = sql2o.open()){
            Integer idOrden = con.createQuery(sql)
                    .addParameter("idCliente", idCliente)
                    .addParameter("estado", "en_proceso")
                    .executeScalar(Integer.class);
            return Optional.ofNullable(idOrden);
        }
    }

    public OrdenEntity findById(Long id) {
        String sql = "SELECT id_orden, id_pedido, fecha_orden, estado, id_cliente, total FROM orden WHERE id_orden = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(OrdenEntity.class);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM orden WHERE id_orden = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }


    public BigDecimal getTotalOrden(Long id) {
        String sql = "SELECT calcular_total_orden(:id)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeScalar(BigDecimal.class);
        }
    }

    public void updateOrden(Long id, OrdenEntity orden) {
        final String updateQuery =
                "UPDATE orden SET fecha_orden = :fecha_orden, estado = :estado, id_pedido = :id_pedido, id_cliente = :id_cliente, total = :total WHERE id_orden = :id";

        try (Connection con = sql2o.beginTransaction()) {
            con.createQuery(updateQuery)
                    .addParameter("id", id)
                    .addParameter("fecha_orden", orden.getFecha_orden())
                    .addParameter("estado", orden.getEstado())
                    .addParameter("id_cliente", orden.getId_cliente())
                    .addParameter("id_pedido", orden.getId_pedido())
                    .addParameter("total", orden.getTotal())
                    .executeUpdate();
            con.commit();
        }
    }

    public OrdenEntity findByPedidoId(Long id) {
        String sql = "SELECT id_orden, id_pedido,fecha_orden, estado, id_cliente, total FROM orden WHERE id_pedido = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(OrdenEntity.class);
        }
    }
}
