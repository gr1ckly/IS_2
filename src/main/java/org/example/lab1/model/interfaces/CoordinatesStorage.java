package org.example.lab1.model.interfaces;

import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dto.FilterOption;

import java.util.List;

public interface CoordinatesStorage {
    long createCoordinates(Coordinates coords) throws Exception;
    Coordinates getCoordinatesByID(long id) throws Exception;
    int getCount(FilterOption... options) throws Exception;
    List<Coordinates> searchCoordinates(int offset, int limit, FilterOption... options) throws Exception;
    int updateCoordinates(long id, Coordinates newCoords) throws Exception;
    int deleteCoordinates(long id) throws Exception;
    void flush() throws Exception;
    void clear() throws Exception;
}
