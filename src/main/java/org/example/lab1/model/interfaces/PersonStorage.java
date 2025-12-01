package org.example.lab1.model.interfaces;

import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;

import java.util.List;

public interface PersonStorage {
    long createPerson(Person person) throws Exception;
    Person getPersonByID(long id) throws Exception;
    int getCount(FilterOption... options) throws Exception;
    List<Person> searchPersons(int offset, int limit, FilterOption... options) throws Exception;
    int updatePerson(long id, Person newPerson) throws Exception;
    int deletePersonByFilter(FilterOption... options) throws Exception;
    void flush() throws Exception;
    void clear() throws Exception;
}
