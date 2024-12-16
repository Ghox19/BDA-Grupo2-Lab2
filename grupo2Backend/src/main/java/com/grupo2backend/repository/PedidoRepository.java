package com.grupo2backend.repository;

import com.grupo2backend.dto.ComunaDTO;
import com.grupo2backend.entity.CategoriaEntity;
import com.grupo2backend.entity.ClienteEntity;
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
        String sql = "SELECT id_pedido, id_zona, id_cliente, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, direccion, estado FROM pedido";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_cliente(result.getLong("id_cliente"));
                        pedido.setEstado(result.getString("estado"));
                        pedido.setDireccion(result.getString("direccion"));

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

    public List<PedidoEntity> findPedidosSinRepartidor() {
        String sql = "SELECT id_pedido, id_zona, id_cliente, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, direccion, estado FROM pedido WHERE id_cliente IS NULL and estado = 'en_rango'";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_cliente(result.getLong("id_cliente"));
                        pedido.setEstado(result.getString("estado"));
                        pedido.setDireccion(result.getString("direccion"));

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
        String sql = "INSERT INTO pedido (id_zona, id_cliente, coordenada_direccion, estado, direccion) " +
                "VALUES (:id_zona, :id_cliente, " +
                "ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326), :estado, : direccion)";
        try (Connection con = sql2o.open()) {
            Map<String, Object> coordJson = entity.getCoordenada_direccion();
            List<Number> coordinates = (List<Number>) coordJson.get("coordinates");

            double longitude = coordinates.get(0).doubleValue();
            double latitude = coordinates.get(1).doubleValue();

            con.createQuery(sql)
                    .addParameter("id_zona", entity.getId_zona())
                    .addParameter("id_cliente", entity.getId_cliente())
                    .addParameter("longitude", longitude)
                    .addParameter("latitude", latitude)
                    .addParameter("estado", entity.getEstado())
                    .addParameter("direccion", entity.getDireccion())
                    .executeUpdate();
        }
    }



    public PedidoEntity findById(Long id) {
        String sql = "SELECT id_pedido, id_zona, id_cliente, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, direccion, estado FROM pedido WHERE id_pedido = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_cliente(result.getLong("id_cliente"));
                        pedido.setEstado(result.getString("estado"));
                        pedido.setDireccion(result.getString("direccion"));

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
                        "id_cliente = :id_cliente, " +
                        "coordenada_direccion = ST_GeomFromText('POINT(' || :longitude || ' ' || :latitude || ')', 4326), " +
                        "estado = :estado, direccion = :direccion WHERE id_pedido = :id_pedido";

        try (Connection con = sql2o.beginTransaction()) {
            Map<String, Object> coordJson = pedidoEntity.getCoordenada_direccion();
            List<Number> coordinates = (List<Number>) coordJson.get("coordinates");
            double longitude = coordinates.get(0).doubleValue();
            double latitude = coordinates.get(1).doubleValue();

            con.createQuery(updateQuery)
                    .addParameter("id_pedido", id_pedido)
                    .addParameter("id_zona", pedidoEntity.getId_zona())
                    .addParameter("id_cliente", pedidoEntity.getId_cliente())
                    .addParameter("longitude", longitude)
                    .addParameter("latitude", latitude)
                    .addParameter("direccion", pedidoEntity.getDireccion())
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

    public List<ClienteEntity> obtenerRepartidoresPorZona(String nombreComuna) {
        String sql = "SELECT * FROM obtener_repartidores_por_comuna(:nombreComuna)";

        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("nombreComuna", nombreComuna)
                    .executeAndFetch(ClienteEntity.class);
        }
    }

    public Boolean esUbicacionRestringida(Integer idPedido) {
        String sql = "SELECT es_ubicacion_restringida(:idPedido)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idPedido", idPedido)
                    .executeScalar(Boolean.class);
        }
    }

    public Boolean esUbicacionGratuita(Integer idPedido) {
        String sql = "SELECT es_area_cobertura(:idPedido)";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idPedido", idPedido)
                    .executeScalar(Boolean.class);
        }
    }

    public List<PedidoEntity> findByRepartidorId(Long id) {
        String sql = "SELECT id_pedido, id_zona, id_cliente, " +
                "ST_AsText(coordenada_direccion) as coordenada_direccion, " +
                "ST_SRID(coordenada_direccion) as srid, direccion, estado FROM pedido WHERE id_cliente = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetch((ResultSetHandler<PedidoEntity>) result -> {
                        PedidoEntity pedido = new PedidoEntity();
                        pedido.setId_pedido(result.getLong("id_pedido"));
                        pedido.setId_zona(result.getLong("id_zona"));
                        pedido.setId_cliente(result.getLong("id_cliente"));
                        pedido.setEstado(result.getString("estado"));
                        pedido.setDireccion(result.getString("direccion"));

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

    public ComunaDTO findZonaNameById(Long id) {
        String sql = "SELECT comuna FROM comunas_santiago WHERE id = :id";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(ComunaDTO.class);
        }
    }
}
