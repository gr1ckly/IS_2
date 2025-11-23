package org.example.lab1.entities.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.lab1.entities.dao.Color;
import org.example.lab1.entities.dao.Country;
import org.example.lab1.entities.dao.Person;

public record PersonDTO(Long id,
                        String name,
                        Long coordinatesId,
                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                        java.util.Date creationDate,
                        Color eyeColor,
                        Color hairColor,
                        Long locationId,
                        Float height,
                        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                        java.time.LocalDateTime birthday,
                        Float weight,
                        Country nationality){
    public Person toDAO() {
        Person newPerson =  new Person();
        newPerson.setName(name);
        newPerson.setEyeColor(eyeColor);
        newPerson.setHairColor(hairColor);
        newPerson.setHeight(height);
        newPerson.setWeight(weight);
        newPerson.setBirthday(birthday);
        newPerson.setNationality(nationality);
        return newPerson;
    }
}
