package com.grupo2backend.controller;

import com.grupo2backend.entity.RepartidorEntity;
import com.grupo2backend.services.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repartidor")
public class RepartidorController {

    @Autowired
    private RepartidorService service;

    @GetMapping
    public List<RepartidorEntity> getAll() {
        return service.getAllRepartidores();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody RepartidorEntity entity) {
        return service.addRepartidor(entity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorEntity> getById(@PathVariable Long id) {
        RepartidorEntity entity = service.getRepartidorById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return service.deleteRepartidor(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategoria(
            @PathVariable("id") Long id_repartidor,
            @RequestBody RepartidorEntity repartidorEntity) {
        try {
            service.updateRepartidor(id_repartidor, repartidorEntity);
            return ResponseEntity.ok("Categoria updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating categoria: " + e.getMessage());
        }
    }
}
