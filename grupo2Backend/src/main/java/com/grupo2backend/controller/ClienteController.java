package com.grupo2backend.controller;

import com.grupo2backend.entity.ClienteEntity;
import com.grupo2backend.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@PreAuthorize("hasRole('admin') or hasRole('cliente') or hasRole('repartidor')")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    public List<ClienteEntity> getAll() {
        return service.getAllClientes();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ClienteEntity entity) {
        return service.addCliente(entity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> getById(@PathVariable Long id) {
        ClienteEntity entity = service.getClienteById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/repartidores")
    public List<?> getAllRepartidores() {
        return service.getAllRepartidores();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.deleteCliente(id);
    }

    @PutMapping("/actualizarEstadoOrden/{id}")
    public ResponseEntity<String> actualizarEstadoOrden(@PathVariable Integer id) {
        String resultado = service.actualizarEstadoOrden(id);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCliente(
            @PathVariable("id") Long id_cliente,
            @RequestBody ClienteEntity cliente) {
        try {
            service.updateCliente(id_cliente, cliente);
            return ResponseEntity.ok("Cliente updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating categoria: " + e.getMessage());
        }
    }

    @GetMapping("/esClienteEnAreaCobertura/{idCliente}/{idPedido}")
    public ResponseEntity<String> esClienteEnAreaCobertura(
            @PathVariable Integer idCliente,
            @PathVariable Integer idPedido) {
        String resultado = service.esClienteEnAreaCobertura(idCliente, idPedido);
        return ResponseEntity.ok(resultado);
    }
}
