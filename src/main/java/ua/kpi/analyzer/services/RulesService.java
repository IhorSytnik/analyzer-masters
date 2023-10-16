package ua.kpi.analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.analyzer.parsers.RulesImplementation;

import java.util.List;
import java.util.Map;

/**
 * @author Ihor Sytnik
 */
@Service
public class RulesService {

    @Autowired
    private RulesImplementation rulesImplementation;

    public void putRules(Map<Integer, List<String>> rules) throws Exception {
        rulesImplementation.putRules(rules);
    }

    public Map<Integer, List<String>> getRules() {
       return rulesImplementation.getClauseRules();
    }

}
