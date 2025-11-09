package org.example.lab1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dto.CoordinatesDTO;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/coordinates")
@Slf4j
public class CoordinatesController {
    private StorageService storageService;

    @Autowired
    public CoordinatesController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountCoordinates(@RequestBody(required = false) FilterOption... options) {
        log.info("getCountCoordinates called with options: {}", (Object) options);
        try{
            int count = this.storageService.getCoordinatesCount(options);
            log.info("getCountCoordinates result: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("getCountCoordinates exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/search_coordinates")
    public ResponseEntity<List<CoordinatesDTO>> searchCoordinates(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) List<FilterOption> options ) {
        log.info("searchCoordinates called with offset: {}, limit: {}, options: {}", offset, limit, options);
        try {
            List<Coordinates> coordinates = this.storageService.searchCoordinates(offset, limit, options.toArray(new FilterOption[0]));
            List<CoordinatesDTO> dtos  = new LinkedList<>();
            for  (Coordinates coord : coordinates) {
                dtos.add(coord.toDTO());
            }
            log.info("searchCoordinates result count: {}", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("searchCoordinates exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create_coordinates")
    public ResponseEntity<Long>  createCoordinates(@RequestBody CoordinatesDTO coordinatesDTO) {
        log.info("createCoordinates called with DTO: {}", coordinatesDTO);
        try{
            long id = this.storageService.createCoordinates(coordinatesDTO.toDAO());
            log.info("createCoordinates result id: {}", id);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            log.error("createCoordinates exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CoordinatesDTO> getCoordinatesById(@PathVariable("id") long id) {
        log.info("getCoordinatesById called with id: {}", id);
        try {
            Coordinates currCoords  = this.storageService.getCoordinatesById(id);
            if (currCoords != null) {
                log.info("getCoordinatesById found coordinates");
                return ResponseEntity.ok(currCoords.toDTO());
            } else {
                log.warn("getCoordinatesById not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("getCoordinatesById exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updateCoordinates(@PathVariable("id") long id, @RequestBody CoordinatesDTO coordinatesDTO) {
        log.info("updateCoordinates called with id: {}, DTO: {}", id, coordinatesDTO);
        try{
            int result = this.storageService.updateCoordinates(id, coordinatesDTO.toDAO());
            log.info("updateCoordinates result: {}", result);
            return ResponseEntity.ok(result);
        } catch (BadDataException e) {
            log.warn("updateCoordinates BadDataException", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("updateCoordinates exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Integer> deleteLocation(@PathVariable("id") long id) {
        log.info("deleteLocation called with id: {}", id);
        try{
            int result = this.storageService.deleteCoordinates(id);
            log.info("deleteLocation result: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("deleteLocation exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
