package com.grupo2backend.controller;

import com.grupo2backend.entity.DetalleOrdenEntity;
import com.grupo2backend.services.DetalleOrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalleOrden")
public class DetalleOrdenController {

    @Autowired
    private DetalleOrdenService service;


    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public List<DetalleOrdenEntity> getAll() {
        return service.getAllDetalleOrden();
    }

    @GetMapping("/orden/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente') or hasRole('repartidor')")
    public ResponseEntity<List<DetalleOrdenEntity>> getDetallesByOrdenId(@PathVariable Long id) {
        List<DetalleOrdenEntity> detalles = service.getDetallesByOrdenId(id);
        return detalles != null ? ResponseEntity.ok(detalles) : ResponseEntity.ok().body(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('cliente')")
    public ResponseEntity<Object> create(@RequestBody DetalleOrdenEntity entity) {
        return service.addDetalleOrden(entity);
    }

    @PutMapping
    @PreAuthorize("hasRole('cliente') or hasRole('admin')")
    public ResponseEntity<DetalleOrdenEntity> update(@RequestBody DetalleOrdenEntity entity) {
        return service.updateDetalleOrden(entity) != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public ResponseEntity<DetalleOrdenEntity> getById(@PathVariable Long id) {
        DetalleOrdenEntity entity = service.getDetalleOrdenById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @GetMapping("/findByProductoAndOrden/{idProducto}/{idOrden}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public ResponseEntity<DetalleOrdenEntity> findByProductoAndOrden(@PathVariable Long idProducto, @PathVariable Long idOrden) {
        DetalleOrdenEntity entity = service.findByProductoAndOrden(idProducto, idOrden);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cliente')")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.deleteDetalleOrden(id);
    }
}
