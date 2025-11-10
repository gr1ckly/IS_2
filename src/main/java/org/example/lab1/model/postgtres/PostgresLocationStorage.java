package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Location;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.entities.dto.OperationType;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.interfaces.LocationStorage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class PostgresLocationStorage implements LocationStorage {
    private static final String alias = "loc";

    @Autowired
    private SQLQueryConstraintConverter<Location> queryConverter;

    @Override
    public long createLocation(Location location) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        try {
            currSession.persist(location);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return location.getId();
    }

    @Override
    public Location getLocationByID(long id) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        Location location;
        try {
            location = currSession.find(Location.class, id);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return location;
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
            query.append(") FROM location ");
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
    public List<Location> searchLocations(int offset, int limit, FilterOption... options) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        List<Location> locations;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM location ");
            query.append(alias);
            Query<Location> newQuery = this.queryConverter.buildQuery(currSession, query, alias, Location.class, options);
            newQuery.setFirstResult(offset);
            newQuery.setMaxResults(limit);
            locations = newQuery.getResultList();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return locations;
    }

    @Override
    public int updateLocation(long id, Location newLocation) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int ans;
        try {
            if (this.getLocationByID(id) != null){
                newLocation.setId(id);
                currSession.merge(newLocation);
                ans = 1;
            }else {
                tx.rollback();
                throw new BadDataException("Location Not Found");
            }
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return ans;
    }

    @Override
    public int deleteLocation(long id) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count;
        try {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM location ");
            query.append(alias);
            count = this.queryConverter.buildQuery(currSession, query, alias, Location.class, new FilterOption("id", OperationType.EQUAL, Long.toString(id))).executeUpdate();
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
