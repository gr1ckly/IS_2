package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.Person;
import org.example.lab1.entities.dto.FilterOption;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.model.interfaces.PersonStorage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class PostgresPersonStorage implements PersonStorage {
    public static final String alias = "pers";

    @Autowired
    private SQLQueryConstraintConverter<Person> queryConverter;

    @Override
    public long createPerson(Person person) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        try {
            currSession.persist(person);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return person.getId();
    }

    @Override
    public Person getPersonByID(long id) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        Person person;
        try {
            person = currSession.find(Person.class, id);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return person;
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
            query.append(") FROM person ");
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
    public List<Person> searchPersons(int offset, int limit, FilterOption... options) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        List<Person> persons;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM person ");
            query.append(alias);
            Query<Person> newQuery = this.queryConverter.buildQuery(currSession, query, alias, Person.class, options);
            newQuery.setFirstResult(offset);
            newQuery.setMaxResults(limit);
            persons = newQuery.getResultList();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            currSession.close();
        }
        return persons;
    }

    @Override
    public int updatePerson(long id, Person newPerson) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count = 0;
        try {
            if (this.getPersonByID(id) != null ) {
                newPerson.setId(id);
                currSession.merge(newPerson);
                count = 1;
            }else {
                tx.rollback();
                throw new BadDataException("Person Not Found");
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
    public int deletePersonByFilter(FilterOption... options) throws Exception {
        Session currSession = HibernateFactory.getSessionFactory().openSession();
        Transaction tx = currSession.beginTransaction();
        int count = 0;
        try {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM person ");
            query.append(alias);
            count = this.queryConverter.buildQuery(currSession, query, alias, Person.class, options).executeUpdate();
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
