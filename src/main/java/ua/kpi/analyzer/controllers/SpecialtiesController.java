package ua.kpi.analyzer.controllers;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import ua.kpi.analyzer.exceptions.WrongFormatSpecialtyException;
import ua.kpi.analyzer.services.SpecialtiesService;

import java.util.Set;

/**
 * <p>Manages specialties.</p>
 * <p>Specialties are needed to check if found publishers belong to the specified specialties.</p>
 * <p>Specialties are <b>NOT</b> necessary.</p>
 *
 * @author Ihor Sytnik
 */
@Controller
@RequestMapping(value = "/specialties")
public class SpecialtiesController {

    @Autowired
    private SpecialtiesService specialtiesService;

    /**
     * <p>Adds specialties.</p>
     *
     * @param specialty specialty number to add
     * @return specialties {@link Set}
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Set<String>> addSpecialty(
            @RequestParam @NotBlank(message = "Specialty may not be empty")
                    String specialty) {
        specialtiesService.addSpecialty(specialty);
        return ResponseEntity.ok(specialtiesService.getSpecialties());
    }

    /**
     * <p>Removes specialties.</p>
     *
     * @param specialty specialty number to remove
     * @return specialties {@link Set}
     */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Set<String>> removeSpecialty(
            @RequestParam @NotBlank(message = "Specialty may not be empty")
                    String specialty) {
        specialtiesService.removeSpecialty(specialty);
        return ResponseEntity.ok(specialtiesService.getSpecialties());
    }
}
