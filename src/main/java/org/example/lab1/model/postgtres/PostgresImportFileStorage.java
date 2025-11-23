package org.example.lab1.model.postgtres;

import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.model.interfaces.ImportFileStorage;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PostgresImportFileStorage implements ImportFileStorage {
    private static final String alias = "files";

    private SQLQueryConstraintConverter<ImportFile> queryConverter;

    @Autowired
    public PostgresImportFileStorage(SQLQueryConstraintConverter<ImportFile> queryConverter) {
        this.queryConverter = queryConverter;
    }

    @Override
    @Transactional
    public long createImportFile(ImportFile file) throws Exception {
        HibernateFactory.getSessionFactory().getCurrentSession().persist(file);
        return file.getId();
    }

    @Override
    @Transactional
    public ImportFile getFileByID(long id) throws Exception {
        return HibernateFactory.getSessionFactory().getCurrentSession().find(ImportFile.class, id);
    }

    @Override
    public int getCount() throws Exception {
        int count = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT COUNT(");
        query.append(alias);
        query.append(") FROM import_files ");
        query.append(alias);
        Query<?> q = this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, null);
        Object res = q.getSingleResult();
        if (res instanceof Number) count = ((Number) res).intValue();
        return count;
    }

    @Override
    @Transactional
    public List<ImportFile> searchImportFiles(int offset, int limit) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM import_files ");
        query.append(alias);
        Query<ImportFile> newQuery = this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, ImportFile.class);
        newQuery.setFirstResult(offset);
        newQuery.setMaxResults(limit);
        return newQuery.getResultList();
    }

    @Override
    @Transactional
    public int updateImportFile(long id, ImportFile newFile) throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM import_files ");
        query.append(alias);
        return this.queryConverter.buildQuery(HibernateFactory.getSessionFactory().getCurrentSession(), query, alias, ImportFile.class).executeUpdate();
    }
}
