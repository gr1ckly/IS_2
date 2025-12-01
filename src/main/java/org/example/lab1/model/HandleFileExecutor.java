package org.example.lab1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.example.lab1.model.interfaces.LocationStorage;
import org.example.lab1.model.interfaces.PersonStorage;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class HandleFileExecutor {
    private ImportFileStorage importFileStorage;

    private CoordinatesStorage coordinatesStorage;

    private PersonStorage personStorage;

    private LocationStorage locationStorage;

    private PersonSimilarity personSimilarity;


    private static int BATCH_SIZE = 1000;

    @Autowired
    public HandleFileExecutor(ImportFileStorage importFileStorage, CoordinatesStorage coordinatesStorage, PersonStorage personStorage, LocationStorage locationStorage, PersonSimilarity personSimilarity) {
        this.importFileStorage = importFileStorage;
        this.personStorage = personStorage;
        this.locationStorage = locationStorage;
        this.coordinatesStorage = coordinatesStorage;
        this.personSimilarity = personSimilarity;
    }

    @Transactional(rollbackFor = Exception.class)
    public int handleFile(InputStream inputStream) throws Exception {
        try {
            LoaderOptions options = new LoaderOptions();
            options.setMaxAliasesForCollections(1000);
            options.setAllowDuplicateKeys(false);
            options.setNestingDepthLimit(3);
            options.setCodePointLimit(300_000_000);
            options.setEnumCaseSensitive(false);
            options.setAllowRecursiveKeys(false);
            Yaml yaml = new Yaml(new PersonYAMLConstructor(options));
            Iterable<Object> docs = yaml.loadAll(inputStream);
            List<Person> persons = this.parsePersons(docs);
            log.error("Parsed all persons");
            return flushPersons(persons);
        } catch (YAMLException | ParseException ye) {
            throw new BadFormatException("Incorrect format");
        }
    }

    private List<Person> parsePersons(Iterable<Object> docs) throws Exception {
        List<Person> persons = new ArrayList<>();
        for (Object obj : docs) {
            if (obj instanceof Person) {
                Person currPerson = (Person) obj;
                if (this.isBadNewPerson(currPerson)) {
                    throw new BadDataException("Not valid Person");
                }
                int count = tryMerge(persons, currPerson);
                if (count == 0) {
                    persons.add(currPerson);
                }
            }
        }
        return persons;
    }

    private int flushPersons(List<Person> persons) throws Exception {
        int count = 0;
        for (Person person : persons) {
            if (person.getLocation() != null) {
                this.locationStorage.createLocation(person.getLocation());
            }
            this.coordinatesStorage.createCoordinates(person.getCoordinates());
            this.personStorage.createPerson(person);
            if (count++ % HandleFileExecutor.BATCH_SIZE == 0) {
                this.locationStorage.flush();
                this.locationStorage.clear();
                this.coordinatesStorage.flush();
                this.coordinatesStorage.clear();
                this.personStorage.flush();
                this.personStorage.clear();
                log.error("Flushed " + count / HandleFileExecutor.BATCH_SIZE + " batch");
            }
        }
        return count;
    }

    private boolean isBadNewPerson(Person newPerson) {
        return newPerson == null || !newPerson.isValid() || newPerson.getId() != null || newPerson.getCreationDate() != null || newPerson.getCoordinates().getId() != null || (newPerson.getLocation() != null && newPerson.getLocation().getId() != null);
    }

    private int tryMerge(List<Person> persons, Person newPerson) {
        int count = 0;
        if (persons != null) {
            for (int i = 0; i < persons.size(); i++) {
                if (this.personSimilarity.areSimilar(newPerson, persons.get(i))) {
                    persons.set(i, this.personSimilarity.merge(newPerson, persons.get(i)));
                    count++;
                }
            }
        }
        return count;
    }
}
