package org.example.lab1.controller;

import org.example.lab1.entities.dao.ImportFile;
import org.example.lab1.entities.dao.ImportStatus;
import org.example.lab1.entities.dto.ImportFileDTO;
import org.example.lab1.exceptions.BadDataException;
import org.example.lab1.exceptions.BadFormatException;
import org.example.lab1.model.ImportFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/files")
public class ImportFileController {
    public ImportFileService importFileService;

    @Autowired
    public ImportFileController(ImportFileService importFileService) {
        this.importFileService = importFileService;
    }

    @GetMapping("/get_count")
    public ResponseEntity<Integer> getCountImportFiles() throws Exception {
        return ResponseEntity.ok(this.importFileService.getImportFilesCount());
    }

    @GetMapping("/search_coordinates")
    public ResponseEntity<List<ImportFileDTO>> searchImportFiles(@RequestParam(name="offset") int offset, @RequestParam(name="limit") int limit) throws Exception {
        List<ImportFile> importFiles = this.importFileService.searchImportFiles(offset, limit);
        List<ImportFileDTO> dtos  = new LinkedList<>();
        for  (ImportFile importFile : importFiles) {
            dtos.add(importFile.toDTO());
        }
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("")
    public ResponseEntity<Integer> importFile(@RequestParam("file") MultipartFile file) throws Exception {
        ImportFile newFile = new ImportFile();
        newFile.setName(file.getName());
        newFile.setStatus(ImportStatus.IN_PROGRESS);
        long id = this.importFileService.createImportFile(newFile);
        try {
            int count = this.importFileService.handleFile(file.getInputStream());
            newFile.setStatus(ImportStatus.SUCCESS);
            newFile.setAddedPersons(count);
            this.importFileService.updateImportFile(id, newFile);
            return ResponseEntity.ok(count);
        } catch (BadFormatException bfe) {
            newFile.setStatus(ImportStatus.BAD_FORMAT);
            this.importFileService.updateImportFile(id, newFile);
            throw bfe;
        } catch (BadDataException bde) {
            newFile.setStatus(ImportStatus.BAD_DATA);
            this.importFileService.updateImportFile(id, newFile);
            throw bde;
        } catch (Exception e) {
            newFile.setStatus(ImportStatus.FAILED);
            this.importFileService.updateImportFile(id, newFile);
            throw e;
        }
    }
}
