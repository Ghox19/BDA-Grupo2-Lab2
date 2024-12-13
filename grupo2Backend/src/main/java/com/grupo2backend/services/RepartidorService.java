package com.grupo2backend.services;

import com.grupo2backend.entity.RepartidorEntity;
import com.grupo2backend.repository.RepartidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;

    @Autowired
    public RepartidorService(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = repartidorRepository;
    }

    public ResponseEntity<Object> addRepartidor(RepartidorEntity repartidorEntity) {
        try {
            repartidorRepository.save(repartidorEntity);
            return new ResponseEntity<>("Se ingresó correctamente el repartidor", HttpStatus.CREATED);
        } catch (Exception e) {
            // Manejo del error
            return new ResponseEntity<>("Error al ingresar el repartidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<RepartidorEntity> getAllRepartidores() {
        return repartidorRepository.findAll();
    }

    public RepartidorEntity getRepartidorById(long id) {
        return repartidorRepository.findById(id);
    }

    public ResponseEntity<Object> deleteRepartidor(long id) {
        RepartidorEntity optionalRepartidor = repartidorRepository.findById(id);
        if (optionalRepartidor != null){
            this.repartidorRepository.deleteById(id);
            return new ResponseEntity<>("Se elimino correctamente el Repartidor", HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public void updateRepartidor(Long id_repartidor, RepartidorEntity repartidorEntity) {
        try {
            repartidorRepository.updateRepartidor(id_repartidor, repartidorEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating categoria: " + e.getMessage());
        }
    }
}
