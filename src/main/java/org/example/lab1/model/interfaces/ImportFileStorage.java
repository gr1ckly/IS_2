package org.example.lab1.model.interfaces;

import org.example.lab1.entities.dao.ImportFile;

import java.util.List;

public interface ImportFileStorage {
    long createImportFile(ImportFile file) throws Exception;
    ImportFile getFileByID(long id) throws Exception;
    int getCount() throws Exception;
    List<ImportFile> searchImportFiles(int offset, int limit) throws Exception;
    int updateImportFile(long id, ImportFile newFile) throws Exception;
    void flush() throws Exception;
    void clear() throws Exception;
}
