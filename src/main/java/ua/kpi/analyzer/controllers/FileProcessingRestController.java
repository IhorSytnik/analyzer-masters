package ua.kpi.analyzer.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.controllers.parameters.FileProcessingParameters;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.services.FileProcessingService;

import java.io.IOException;

/**
 * <p>Processes surveys</p>
 *
 * @author Ihor Sytnik
 */
@RestController
@Validated
@RequestMapping(value = "/processing")
public class FileProcessingRestController {
    @Autowired
    private FileProcessingService fileProcessingService;

    /**
     * <p>Processes surveys</p>
     *
     * @param fileProcessingParameters contains filename - file name of the survey
     * @return {@link Author} object that has all the processing results.
     */
    @PostMapping
    public ResponseEntity<Author> process(
            @RequestBody @Valid FileProcessingParameters fileProcessingParameters) throws IOException, InterruptedException {
        fileProcessingService.process(fileProcessingParameters.filename());
        return ResponseEntity.ok().body(fileProcessingService.getAuthor());
    }
}
