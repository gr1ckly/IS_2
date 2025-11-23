package org.example.lab1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.example.lab1.model.interfaces.LocationStorage;
import org.example.lab1.model.interfaces.PersonStorage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
@Getter
@Setter
public class ImportFileService {
    private PersonStorage personStorage;

    private LocationStorage locationStorage;

    private CoordinatesStorage coordinatesStorage;

    private ImportFileStorage importFileStorage;

    private NotificationService notificationService;

    private PersonSimilarity personSimilarity;

    private static final String importFilesMessage = "import_file";

    private static final double similarTreshold = 0.85;

    @Autowired
    public ImportFileService(PersonStorage personStorage, LocationStorage locationStorage, CoordinatesStorage coordinatesStorage, ImportFileStorage importFilesStorage, NotificationService notificationService, PersonSimilarity personSimilarity) {
        this.personStorage = personStorage;
        this.locationStorage = locationStorage;
        this.coordinatesStorage = coordinatesStorage;
        this.importFileStorage = importFilesStorage;
        this. notificationService = notificationService;
        this.personSimilarity = personSimilarity;
    }

    public int getImportFilesCount() throws Exception {
        return this.importFileStorage.getCount();
    }

    public List<ImportFile> searchImportFiles(int offset, int limit) throws Exception {
        return this.importFileStorage.searchImportFiles(offset, limit);
    }

    public long createImportFile(ImportFile file) throws Exception {
        long id = this.importFileStorage.createImportFile(file);
        this.notificationService.sendMessage(ImportFileService.importFilesMessage);
        return id;
    }

    public int updateImportFile(Long id, ImportFile newFile) throws Exception {
        int count = this.importFileStorage.updateImportFile(id, newFile);
        this.notificationService.sendMessage(ImportFileService.importFilesMessage);
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    public int handleFile(InputStream inputStream) throws Exception {
        try {
            Yaml yaml = new Yaml();
            Iterable<Object> docs = yaml.loadAll(inputStream);
            List<Person> persons = this.parsePersons(docs);
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
            count++;
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
                if (this.personSimilarity.similarScore(newPerson, persons.get(i)) >= ImportFileService.similarTreshold) {
                    persons.set(i, this.personSimilarity.merge(newPerson, persons.get(i)));
                    count++;
                }
            }
        }
        return count;
    }
}
