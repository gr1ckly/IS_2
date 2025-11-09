package org.example.lab1.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.PersonDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/persons")
@Slf4j
public class PersonController {
    private StorageService storageService;

    @Autowired
    public PersonController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/get_count")
    public ResponseEntity<Integer> getCountPersons(@RequestBody(required = false) FilterOption... options) {
        try{
            return ResponseEntity.ok(this.storageService.getPersonCount(options));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/search_persons")
    public ResponseEntity<List<PersonDTO>> searchPersons(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit, @RequestBody(required = false) FilterOption... options) {
        log.info("searchPersons called with offset: {}, limit: {}, options: {}", offset, limit, options);
        try {
            List<Person> persons  = this.storageService.searchPersons(offset, limit, options);
            log.info("searchPersons result: {}", persons);
            List<PersonDTO> dtos  = new LinkedList<>();
            for  (Person pers : persons) {
                dtos.add(pers.toDTO());
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("searchPersons exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create_person")
    public ResponseEntity<Long>  createPerson(@RequestBody PersonDTO personDTO) {
        log.info("persoDto: {}", personDTO);
        try{
            return ResponseEntity.ok(this.storageService.createPerson(personDTO.toDAO(), personDTO.locationId(), personDTO.coordinatesId()));
        } catch (BadDataException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("createPerson exception", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable("id") long id) {
        try {
            Person pers  = this.storageService.getPersonById(id);
            if (pers != null) {
                return ResponseEntity.ok(pers.toDTO());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Integer> updatePerson(@PathVariable("id") long id, @RequestBody PersonDTO personDTO) {
        try{
            return ResponseEntity.ok(this.storageService.updatePerson(id, personDTO.toDAO(), personDTO.locationId(), personDTO.coordinatesId()));
        } catch (BadDataException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<Integer> deletePersons(@RequestBody FilterOption... options) {
        try{
            return ResponseEntity.ok(this.storageService.deletePersonsByFilters(options));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
