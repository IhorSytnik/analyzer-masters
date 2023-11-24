package ua.kpi.analyzer.controllers;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.services.SpecialtiesService;

import java.util.Set;

/**
 * <p>Manages specialties.</p>
 * <p>Specialties are needed to check if found publishers belong to the specified specialties.</p>
 * <p>Setting specialties is <b>not</b> necessary.</p>
 *
 * @author Ihor Sytnik
 */
@RestController
@Validated
@RequestMapping(value = "/specialties")
public class SpecialtiesController {

    @Autowired
    private SpecialtiesService specialtiesService;

    /**
     * <p>Get specialties.</p>
     *
     * @return specialties {@link Set}
     */
    @GetMapping
    public ResponseEntity<Set<String>> getSpecialties() {
        return ResponseEntity.ok(specialtiesService.getSpecialties());
    }

    /**
     * <p>Adds specialties.</p>
     *
     * @param specialties {@link Set} of specialty numbers to add
     * @return specialties {@link Set}
     */
    @PostMapping(path = "/add")
    public ResponseEntity<Set<String>> addSpecialty(
            @RequestBody @NotEmpty(message = "Specialties set may not be empty")
                    Set<String> specialties) {
        specialtiesService.addSpecialties(specialties);
        return ResponseEntity.ok(specialtiesService.getSpecialties());
    }

    /**
     * <p>Removes specialties.</p>
     *
     * @param specialties {@link Set} of specialty numbers to remove
     * @return specialties {@link Set}
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Set<String>> removeSpecialty(
            @RequestBody @NotEmpty(message = "Specialties set may not be empty")
                    Set<String> specialties) {
        specialtiesService.removeSpecialties(specialties);
        return ResponseEntity.ok(specialtiesService.getSpecialties());
    }
}
