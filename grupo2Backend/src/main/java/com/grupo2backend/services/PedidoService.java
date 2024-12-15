package com.grupo2backend.services;

import com.grupo2backend.entity.ClienteEntity;
import com.grupo2backend.entity.PedidoEntity;
import com.grupo2backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public ResponseEntity<Object> addPedido(PedidoEntity pedidoEntity) {
        try {
            pedidoRepository.save(pedidoEntity);
            return new ResponseEntity<>("Se ingresó correctamente el pedido", HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejo del error
            return new ResponseEntity<>("Error al ingresar el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<PedidoEntity> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public List<PedidoEntity> getPedidosSinRepartidor() {
        return pedidoRepository.findPedidosSinRepartidor();
    }

    public PedidoEntity getPedidoById(long id) {
        return pedidoRepository.findById(id);
    }

    public ResponseEntity<Object> deletePedido(long id) {
        PedidoEntity optionalPedido = pedidoRepository.findById(id);
        if (optionalPedido != null){
            this.pedidoRepository.deleteById(id);
            return new ResponseEntity<>("Se elimino correctamente el Pedido", HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public void updatePedido(Long id_pedido, PedidoEntity pedidoEntity) {
        try {
            pedidoRepository.updatePedido(id_pedido, pedidoEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating pedido: " + e.getMessage());
        }
    }

    public Boolean verificarYActualizarEstado(Integer idPedido) {
        return pedidoRepository.verificarYActualizarEstadoPedido(idPedido);
    }

    public ResponseEntity<Object> obtenerRepartidoresPorZona(String nombreComuna) {
        try {
            List<ClienteEntity> repartidores = pedidoRepository.obtenerRepartidoresPorZona(nombreComuna);
            if (repartidores.isEmpty()) {
                return new ResponseEntity<>("No se encontraron repartidores en la comuna", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(repartidores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al obtener repartidores: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String esUbicacionRestringida(Integer idPedido) {
        return pedidoRepository.esUbicacionRestringida(idPedido);
    }
}
