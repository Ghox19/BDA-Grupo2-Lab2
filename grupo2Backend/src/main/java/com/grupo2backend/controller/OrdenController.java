package com.grupo2backend.controller;

import com.grupo2backend.dto.OrdenInDTO;
import com.grupo2backend.entity.OrdenEntity;
import com.grupo2backend.services.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orden")
public class OrdenController {

    @Autowired
    private OrdenService service;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public List<OrdenEntity> getAll() {
        return service.getAllOrdenes();
    }

    @PostMapping
    @PreAuthorize("hasRole('cliente') or hasRole('admin')")
    public ResponseEntity<Object> create(@RequestBody OrdenInDTO entity) {
        return service.addOrden(entity);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente') or hasRole('repartidor')")
    public ResponseEntity<OrdenEntity> getById(@PathVariable Long id) {
        OrdenEntity entity = service.getOrdenById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public List<OrdenEntity> getOrdenesByClienteId(@PathVariable Long idCliente) {
        return service.getOrdenesByClienteId(idCliente);
    }
    @GetMapping("/ordencliente/{id_cliente}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public Optional<Integer> getOrdenById(@PathVariable Long id_cliente){
        return service.getOrdenProcesoByIdCliente(id_cliente);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.deleteOrden(id);
    }

    @PutMapping("/calcularTotalOrden/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public ResponseEntity<Object> getTotalOrden(@PathVariable Long id) {
        OrdenEntity orden = service.getOrdenById(id);

        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada");
        }

        BigDecimal totalOrden = service.getTotalOrden(id);

        return ResponseEntity.ok(totalOrden);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente') or hasRole('repartidor')")
    public ResponseEntity<String> updateOrden(
            @PathVariable("id") Long id,
            @RequestBody OrdenEntity orden) {
        try {
            service.updateOrden(id, orden);
            return ResponseEntity.ok("Orden updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating orden: " + e.getMessage());
        }
    }

    @GetMapping("/pedido/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('repartidor')")
    public ResponseEntity<OrdenEntity> getByPedidoId(@PathVariable Long id) {
        OrdenEntity entity = service.getByPedidoId(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
}
