package ua.kpi.analyzer.controllers;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.services.FileProcessingService;
import ua.kpi.analyzer.things.Author;

import java.io.IOException;

/**
 * <p>Processes surveys</p>
 *
 * @author Ihor Sytnik
 */
@Controller
@RequestMapping(value = "/processing")
public class FileProcessingController {
    @Autowired
    private FileProcessingService fileProcessingService;

    /**
     * <p>Processes surveys</p>
     *
     * @param filename file name of the survey
     * @return {@link Author} object that has all the processing results.
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Author> process(
            @RequestParam @NotBlank(message = "File name may not be empty")
                    String filename) {
        try {
            fileProcessingService.process(filename);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.badRequest().body(fileProcessingService.getAuthor());
        }
        return ResponseEntity.ok().body(fileProcessingService.getAuthor());
    }
}
