package com.grupo2backend.repository;

import com.grupo2backend.entity.PedidoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PedidoRepository {

    @Autowired
    private Sql2o sql2o;

    public List<PedidoEntity> findAll() {
        String sql = "SELECT id_pedido, id_zona, id_repartidor, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, estado FROM pedido";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_repartidor(result.getLong("id_repartidor"));
                        pedido.setEstado(result.getString("estado"));

                        String wkt = result.getString("coordenada_direccion");
                        if (wkt != null) {
                            String[] coords = wkt.substring(6, wkt.length() - 1).split(" ");
                            Map<String, Object> coordMap = new HashMap<>();
                            coordMap.put("type", "Point");
                            coordMap.put("coordinates", Arrays.asList(
                                    Double.parseDouble(coords[0]),
                                    Double.parseDouble(coords[1])
                            ));
                            coordMap.put("srid", result.getInt("srid"));
                            pedido.setCoordenada_direccion(coordMap);
                        }
                        return pedido;
                    });
        }
    }

    public void save(PedidoEntity entity) {
        String sql = "INSERT INTO pedido (id_zona, id_repartidor, coordenada_direccion, estado) " +
                "VALUES (:id_zona, :id_repartidor, " +
                "ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326), :estado)";
        try (Connection con = sql2o.open()) {
            Map<String, Object> coordJson = entity.getCoordenada_direccion();
            List<Number> coordinates = (List<Number>) coordJson.get("coordinates");

            double longitude = coordinates.get(0).doubleValue();
            double latitude = coordinates.get(1).doubleValue();

            con.createQuery(sql)
                    .addParameter("id_zona", entity.getId_zona())
                    .addParameter("id_repartidor", entity.getId_repartidor())
                    .addParameter("longitude", longitude)
                    .addParameter("latitude", latitude)
                    .addParameter("estado", entity.getEstado())
                    .executeUpdate();
        }
    }



    public PedidoEntity findById(Long id) {
        String sql = "SELECT id_pedido, id_zona, id_repartidor, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, estado FROM pedido WHERE id_pedido = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_repartidor(result.getLong("id_repartidor"));
                        pedido.setEstado(result.getString("estado"));

                        String wkt = result.getString("coordenada_direccion");
                        if (wkt != null) {
                            String[] coords = wkt.substring(6, wkt.length() - 1).split(" ");
                            Map<String, Object> coordMap = new HashMap<>();
                            coordMap.put("type", "Point");
                            coordMap.put("coordinates", Arrays.asList(
                                    Double.parseDouble(coords[0]),
                                    Double.parseDouble(coords[1])
                            ));
                            coordMap.put("srid", result.getInt("srid"));
                            pedido.setCoordenada_direccion(coordMap);
                        }
                        return pedido;
                    });
        }
    }


    public void deleteById(Long id) {
        String sql = "DELETE FROM pedido WHERE id_pedido = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }

    public void updatePedido(Long id_pedido, PedidoEntity pedidoEntity) {
        final String updateQuery =
                "UPDATE pedido SET id_zona = :id_zona, " +
                        "id_repartidor = :id_repartidor, " +
                        "coordenada_direccion = ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326), " +
                        "estado = :estado WHERE id_pedido = :id_pedido";

        try (Connection con = sql2o.beginTransaction()) {
            Map<String, Object> coordJson = pedidoEntity.getCoordenada_direccion();
            List<Number> coordinates = (List<Number>) coordJson.get("coordinates");
            double longitude = coordinates.get(0).doubleValue();
            double latitude = coordinates.get(1).doubleValue();

            con.createQuery(updateQuery)
                    .addParameter("id_pedido", id_pedido)
                    .addParameter("id_zona", pedidoEntity.getId_zona())
                    .addParameter("id_repartidor", pedidoEntity.getId_repartidor())
                    .addParameter("longitude", longitude)
                    .addParameter("latitude", latitude)
                    .addParameter("estado", pedidoEntity.getEstado())
                    .executeUpdate();
            con.commit();
        }
    }

    public boolean verificarYActualizarEstadoPedido(Integer idPedido) {
        String sql = "SELECT verificar_y_actualizar_estado_pedido(:idPedido)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idPedido", idPedido)
                    .executeScalar(Boolean.class);
        }
    }

    public String esUbicacionRestringida(Integer idPedido) {
        String sql = "SELECT es_ubicacion_restringida(:idPedido)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idPedido", idPedido)
                    .executeScalar(String.class);
        }
    }
}
