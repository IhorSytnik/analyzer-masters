package ua.kpi.analyzer.controllers.deprecated;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.kpi.analyzer.services.RulesService;

import java.util.List;
import java.util.Map;

/**
 * @author Ihor Sytnik
 */
//@Controller
//@Validated
//@RequestMapping(value = "/rules")
@Deprecated
public class RulesController {

    @Autowired
    private RulesService rulesService;

    @PostMapping("/put")
    @ResponseBody
    public ResponseEntity<Map<Integer, List<String>>> putRules(
            @RequestBody @NotEmpty(message = "Rules parameter may not be empty")
                    Map<Integer, List<String>> rules) throws Exception {
        rulesService.putRules(rules);
        return ResponseEntity.ok(rulesService.getRules());
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<Integer, List<String>>> getRules() {
        return ResponseEntity.ok(rulesService.getRules());
    }
}
