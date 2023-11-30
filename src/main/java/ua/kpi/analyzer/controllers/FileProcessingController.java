package ua.kpi.analyzer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.services.FileProcessingService;

import java.io.IOException;
import java.util.Set;

/**
 * <p>Processes surveys</p>
 *
 * @author Ihor Sytnik
 */
@RestController
@Validated
@CrossOrigin({"${cors.allowed-origins}"})
@RequestMapping(value = "/api/processing")
public class FileProcessingController {
    @Autowired
    private FileProcessingService fileProcessingService;

    /**
     * <p>Processes surveys</p>
     *
     * @param file represents a file of survey
     * @return {@link Author} object that has all the processing results.
     */
    @PostMapping
    public ResponseEntity<Author> process(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "specialties", required = false) Set<String> specialties) throws IOException {
        fileProcessingService.process(file.getInputStream(), specialties);
        return ResponseEntity.ok().body(fileProcessingService.getAuthor());
    }
}
