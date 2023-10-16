package ua.kpi.analyzer.controllers;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.services.RulesService;

import java.util.List;
import java.util.Map;

/**
 * @author Ihor Sytnik
 */
@RestController
@Validated
@RequestMapping(value = "/rules")
public class RulesRestController {

    @Autowired
    private RulesService rulesService;

    @PostMapping("/update")
    public ResponseEntity<Map<Integer, List<String>>> putRules(
            @RequestBody @NotEmpty(message = "Rules parameter may not be empty")
                    Map<Integer, List<String>> rules) throws Exception {
        rulesService.putRules(rules);
        return ResponseEntity.ok(rulesService.getRules());
    }

    @GetMapping
    public ResponseEntity<Map<Integer, List<String>>> getRules() {
        return ResponseEntity.ok(rulesService.getRules());
    }
}
