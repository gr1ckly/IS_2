package org.example.lab1.controller;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.LocationDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    private StorageService storageService;

    @Autowired
    public LocationController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountLocations(@RequestBody(required = false) FilterOption... options) {
        try{
            return ResponseEntity.ok(this.storageService.getLocationCount(options));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/search_locations")
    public ResponseEntity<List<LocationDTO>> searchLocations(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) FilterOption... options) {
        try {
            List<Location> locations = this.storageService.searchLocations(offset, limit, options);
            List<LocationDTO> dtos  = new LinkedList<>();
            for  (Location location : locations) {
                dtos.add(location.toDTO());
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create_location")
    public ResponseEntity<Long>  createLocation(@RequestBody LocationDTO locationDTO) {
        try{
            return ResponseEntity.ok(this.storageService.createLocation(locationDTO.toDAO()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable("id") long id) {
        try {
            Location currLocation  = this.storageService.getLocationById(id);
            if (currLocation != null) {
                return ResponseEntity.ok(currLocation.toDTO());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updateLocation(@PathVariable("id") long id, @RequestBody LocationDTO locationDTO) {
        try{
            return ResponseEntity.ok(this.storageService.updateLocation(id, locationDTO.toDAO()));
        } catch (BadDataException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Integer> deleteLocation(@PathVariable("id") long id) {
        try{
            return ResponseEntity.ok(this.storageService.deleteLocation(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
