package org.example.lab1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.example.lab1.model.interfaces.LocationStorage;
import org.example.lab1.model.interfaces.PersonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Notification;
import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StorageService {

    private PersonStorage personStorage;

    private LocationStorage locationStorage;

    private CoordinatesStorage coordinatesStorage;

    private NotificationService notificationService;

    private static final String personMessage = "person";

    private static final String locationMessage = "location";

    private static final String coordinatesMessage = "coordinates";

    @Autowired
    public StorageService(NotificationService notificationService, PersonStorage personStorage, CoordinatesStorage coordinatesStorage, LocationStorage locationStorage) {
        this.personStorage = personStorage;
        this.locationStorage = locationStorage;
        this.coordinatesStorage = coordinatesStorage;
        this.notificationService = notificationService;
    }

    public long createPerson(Person newPerson, Long locationId, long coordinatesId) throws Exception {
        if (locationId != null && locationId > 0) {
            Location currLocation = this.locationStorage.getLocationByID(locationId);
            if (currLocation == null) {
                throw new BadDataException("Location not found");
            }
            newPerson.setLocation(currLocation);
        }
        Coordinates currCoords = this.coordinatesStorage.getCoordinatesByID(coordinatesId);
        if (currCoords == null) {
            throw new BadDataException("Coordinates not found");
        }
        newPerson.setCoordinates(currCoords);
        long createdId = this.personStorage.createPerson(newPerson);
        notificationService.sendMessage(StorageService.personMessage);
        return createdId;
    }

    public Person getPersonById(long id) throws Exception {
        return this.personStorage.getPersonByID(id);
    }

    public int getPersonCount(FilterOption... options) throws Exception {
        return this.personStorage.getCount(options);
    }

    public List<Person> searchPersons(int offset, int limit, FilterOption... options) throws Exception{
        return this.personStorage.searchPersons(offset, limit, options);
    }

    public int updatePerson(long id, Person newPerson, Long locationId, long coordinatesId) throws Exception {
        if (locationId != null && locationId > 0) {
            Location currLocation = this.locationStorage.getLocationByID(locationId);
            if (currLocation == null) {
                throw new BadDataException("Location not found");
            }
            newPerson.setLocation(currLocation);
        }
        Coordinates currCoords = this.coordinatesStorage.getCoordinatesByID(coordinatesId);
        if (currCoords == null) {
            throw new BadDataException("Coordinates not found");
        }
        newPerson.setCoordinates(currCoords);
        int updated = this.personStorage.updatePerson(id, newPerson);
        if (updated > 0) {
            notificationService.sendMessage(StorageService.personMessage);
        }
        return updated;
    }

    public int deletePersonsByFilters(FilterOption... options) throws Exception {
        int deleted = this.personStorage.deletePersonByFilter(options);
        if (deleted > 0) {
            notificationService.sendMessage(StorageService.personMessage);
        }
        return deleted;
    }

    public long createLocation(Location newLocation) throws Exception {
        long createdId = this.locationStorage.createLocation(newLocation);
        notificationService.sendMessage(StorageService.locationMessage);
        return createdId;
    }

    public Location getLocationById(long id) throws Exception {
        return this.locationStorage.getLocationByID(id);
    }

    public int getLocationCount(FilterOption... options) throws Exception {
        return this.locationStorage.getCount(options);
    }

    public List<Location> searchLocations(int offset, int limit, FilterOption... options) throws Exception{
        return this.locationStorage.searchLocations(offset, limit, options);
    }

    public int updateLocation(long id, Location newLocation) throws Exception {
        int updated = this.locationStorage.updateLocation(id, newLocation);
        if (updated > 0) {
            notificationService.sendMessage(StorageService.locationMessage);
        }
        return updated;
    }

    public int deleteLocation(long id) throws Exception {
        int deleted = this.locationStorage.deleteLocation(id);
        if (deleted > 0) {
            notificationService.sendMessage(StorageService.locationMessage);
        }
        return deleted;
    }

    public long createCoordinates(Coordinates newCoordinates) throws Exception {
        long createdId = this.coordinatesStorage.createCoordinates(newCoordinates);
        notificationService.sendMessage(StorageService.coordinatesMessage);
        return createdId;
    }

    public Coordinates getCoordinatesById(long id) throws Exception {
        return this.coordinatesStorage.getCoordinatesByID(id);
    }

    public int getCoordinatesCount(FilterOption... options) throws Exception {
        return this.coordinatesStorage.getCount(options);
    }

    public List<Coordinates> searchCoordinates(int offset, int limit, FilterOption... options) throws Exception{
        return this.coordinatesStorage.searchCoordinates(offset, limit, options);
    }

    public int updateCoordinates(long id, Coordinates newCoordinates) throws Exception {
        int updated = this.coordinatesStorage.updateCoordinates(id, newCoordinates);
        if (updated > 0) {
            notificationService.sendMessage(StorageService.coordinatesMessage);
        }
        return updated;
    }

    public int deleteCoordinates(long id) throws Exception {
        int deleted = this.coordinatesStorage.deleteCoordinates(id);
        if (deleted > 0) {
            notificationService.sendMessage(StorageService.coordinatesMessage);
        }
        return deleted;
    }
}

