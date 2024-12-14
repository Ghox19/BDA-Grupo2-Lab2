package com.grupo2backend.controller;

import com.grupo2backend.entity.PedidoEntity;
import com.grupo2backend.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public List<PedidoEntity> getAll() {
        return service.getAllPedidos();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody PedidoEntity entity) {
        return service.addPedido(entity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoEntity> getById(@PathVariable Long id) {
        PedidoEntity entity = service.getPedidoById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.deletePedido(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePedido(
            @PathVariable("id") Long id_pedido,
            @RequestBody PedidoEntity pedidoEntity) {
        try {
            service.updatePedido(id_pedido, pedidoEntity);
            return ResponseEntity.ok("Pedido updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating pedido: " + e.getMessage());
        }
    }

    @PutMapping("/verificarEstado/{id}")
    public Boolean verificarYActualizarEstado(@PathVariable("id") Integer idPedido) {
        return service.verificarYActualizarEstado(idPedido);
    }

    @GetMapping("/repartidoresEnZona")
    public ResponseEntity<Object> obtenerRepartidoresPorZona(@RequestParam String nombreComuna) {
        return service.obtenerRepartidoresPorZona(nombreComuna);
    }
}
