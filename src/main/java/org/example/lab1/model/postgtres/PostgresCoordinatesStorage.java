package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Coordinates;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.OperationType;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.interfaces.CoordinatesStorage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class PostgresCoordinatesStorage implements CoordinatesStorage {
    private static final String alias = "coords";

    @Autowired
    private SQLQueryConstraintConverter<Coordinates> queryConverter;

    @Override
    public long createCoordinates(Coordinates coords) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        try {
            currSession.persist(coords);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return coords.getId();
    }

    @Override
    public Coordinates getCoordinatesByID(long id) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        Coordinates coords = null;
        try {
            coords = currSession.find(Coordinates.class, id);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return coords;
    }

    @Override
    public int getCount(FilterOption... options) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count = 0;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT COUNT(");
            query.append(alias);
            query.append(") FROM coordinates ");
            query.append(alias);
            Query<?> q = this.queryConverter.buildQuery(currSession, query, alias, null, options);
            Object res = q.getSingleResult();
            if (res instanceof Number) count = ((Number) res).intValue();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return count;
    }

    @Override
    public List<Coordinates> searchCoordinates(int offset, int limit, FilterOption... options) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        List<Coordinates> coords = null;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM coordinates ");
            query.append(alias);
            if (options != null) {
                Query<Coordinates> newQuery = this.queryConverter.buildQuery(currSession, query, alias, Coordinates.class, options);
                newQuery.setFirstResult(offset);
                newQuery.setMaxResults(limit);
                coords = newQuery.getResultList();
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return coords;
    }

    @Override
    public int updateCoordinates(long id, Coordinates newCoords) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count;
        try {
            if (this.getCoordinatesByID(id) != null) {
                newCoords.setId(id);
                currSession.merge(newCoords);
                count = 1;
            } else {
                tx.rollback();
                throw new BadDataException("Coordinates Not Found");
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return count;
    }

    @Override
    public int deleteCoordinates(long id) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count;
        try {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM coordinates ");
            query.append(alias);
            count = this.queryConverter.buildQuery(currSession, query, alias, Coordinates.class, new FilterOption("id", OperationType.EQUAL, Long.toString(id))).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return count;
    }
}
