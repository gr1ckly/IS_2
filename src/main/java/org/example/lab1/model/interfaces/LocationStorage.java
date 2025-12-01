package org.example.lab1.model.interfaces;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;

import java.util.List;

public interface LocationStorage {
    long createLocation(Location location) throws Exception;
    Location getLocationByID(long id) throws Exception;
    int getCount(FilterOption... options) throws Exception;
    List<Location> searchLocations(int offset, int limit, FilterOption... options) throws Exception;
    int updateLocation(long id, Location newLocation) throws Exception;
    int deleteLocation(long id) throws Exception;
    void flush() throws Exception;
    void clear() throws Exception;
}
