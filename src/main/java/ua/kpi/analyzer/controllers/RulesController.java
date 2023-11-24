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
 * <p>Survey analyzer rules</p>
 *
 * @author Ihor Sytnik
 */
@RestController
@Validated
@RequestMapping(value = "/rules")
public class RulesController {

    @Autowired
    private RulesService rulesService;

    /**
     * <p>Updates survey analyzer rules.</p>
     * <p>Specified clauses will replace old ones or create new and
     * if clauses weren't specified those will remain.<br>
     * <i>Example:</i> if application already has these rules:<br>
     * {@code {
     *     '1': ['cit:L5y'],
     *     '2': ['cit:L5y', 'min:3'],
     *     '3': ['cit']
     * }}<br>
     * And the request has these rules:<br>
     * {@code {
     *     '1': ['cit:L1y', 'req'],
     *     '2': ['min:5'],
     *     '5': ['req']
     * }}<br>
     * The resulting map will be:<br>
     * {@code {
     *     '1': ['cit:L1y', 'req'],
     *     '2': ['min:5'],
     *     '3': ['cit'],
     *     '5': ['req']
     * }}
     * </p>
     * <p><b><u>Possible rules:</u></b>
     * <ul>
     *     <li><i>'cit'</i> - parses subclauses as citations;</li>
     *     <li><i>'cit:L<b>N</b>y'</i> - same as cit, but also specifies
     *      publishing year to be in the past <b>N</b> years;</li>
     *     <li><i>'req'</i> - specifies clause to be required;</li>
     *     <li><i>'min:<b>N</b>'</i> - specifies clause to contain <b>N</b>
     *     subclauses at minimum;</li>
     * </ul></p>
     *
     * @param rules {@link Map} of clause numbers with rules.
     * @return rules, currently set in the program.
     * @throws Exception if rule had a bad format.
     */
    @PostMapping("/update")
    public ResponseEntity<Map<Integer, List<String>>> putRules(
            @RequestBody @NotEmpty(message = "Rules parameter may not be empty")
                    Map<Integer, List<String>> rules) throws Exception {
        rulesService.putRules(rules);
        return ResponseEntity.ok(rulesService.getRules());
    }

    /**
     * <p>Returns rules, currently set in the program.</p>
     *
     * @return rules, currently set in the program.
     */
    @GetMapping
    public ResponseEntity<Map<Integer, List<String>>> getRules() {
        return ResponseEntity.ok(rulesService.getRules());
    }

//    todo add removeRules method
}
