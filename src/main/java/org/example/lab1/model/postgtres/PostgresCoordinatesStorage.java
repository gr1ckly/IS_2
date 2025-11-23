package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.OperationType;
import org.example.lab1.exceptions.NotFoundException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresCoordinatesStorage implements CoordinatesStorage {
    private static final String alias = "coords";

    private SQLQueryConstraintConverter<Coordinates> queryConverter;

    @Autowired
    public PostgresCoordinatesStorage(SQLQueryConstraintConverter<Coordinates> queryConverter) {
        this.queryConverter = queryConverter;
    }

    @Override
    @Transactional
    public long createCoordinates(Coordinates coords) throws Exception {
        HibernateFactory.getSessionFactory().getCurrentSession().persist(coords);
        return coords.getId();
    }

    @Override
    @Transactional
    public Coordinates getCoordinatesByID(long id) throws Exception {
        return  HibernateFactory.getSessionFactory().getCurrentSession().find(Coordinates.class, id);
    }

    @Override
    @Transactional
    public int getCount(FilterOption... options) throws Exception {
        int count = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(");
        query.append(alias);
        query.append(") FROM coordinates ");
        query.append(alias);
        Query<?> q = this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, null, options);
        Object res = q.getSingleResult();
        if (res instanceof Number) count = ((Number) res).intValue();
        return count;
    }

    @Override
    @Transactional
    public List<Coordinates> searchCoordinates(int offset, int limit, FilterOption... options) throws Exception {
        List<Coordinates> coords = null;
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM coordinates ");
        query.append(alias);
        if (options != null) {
            Query<Coordinates> newQuery = this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, Coordinates.class, options);
            newQuery.setFirstResult(offset);
            newQuery.setMaxResults(limit);
            coords = newQuery.getResultList();
        }
        return coords;
    }

    @Override
    @Transactional
    public int updateCoordinates(long id, Coordinates newCoords) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().getCurrentSession();
        if (currSession.find(Coordinates.class, id) != null) {
            newCoords.setId(id);
            currSession.merge(newCoords);
            return 1;
        } else {
            throw new NotFoundException("Coordinates Not Found");
        }
    }

    @Override
    @Transactional
    public int deleteCoordinates(long id) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM coordinates ");
        query.append(alias);
        return this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, Coordinates.class, new FilterOption("id", OperationType.EQUAL, Long.toString(id))).executeUpdate();
    }
}
