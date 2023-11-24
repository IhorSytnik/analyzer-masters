package ua.kpi.analyzer.parsers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.grobid.core.data.BiblioItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.exceptions.WrongRuleSyntaxException;
import ua.kpi.analyzer.requests.RegisterProcessor;
import ua.kpi.analyzer.entities.ADocument;
import ua.kpi.analyzer.entities.Author;

import java.util.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Component
public class RulesImplementation {

    @Setter
    private Author author;
    @Getter
    private Map<Integer, List<Consumer<ADocument>>> ruleMap = new HashMap<>();
    @Autowired
    private RegisterProcessor registerProcessor;
    @Setter
    private Set<String> specialtiesToCheckFor;
    private Properties locText;

    Set<Integer> citationNums;

    @Getter
    Map<Integer, List<String>> clauseRules;

    public RulesImplementation(
            @Autowired Set<Integer> citationNums,
            @Autowired Map<Integer, List<String>> clauseRules,
            @Autowired Properties locText) throws Exception {

        this.locText = locText;
        this.citationNums = citationNums;
        this.clauseRules = clauseRules;
        updateRules();
    }

    public void putRules(Map<Integer, List<String>> newRules) throws Exception {
        clauseRules.putAll(newRules);
        updateRules();
    }

    public void updateRules() throws Exception {
        for (var entryInt : clauseRules.entrySet()) {
            List<Consumer<ADocument>> predicateList = new ArrayList<>();
            for (var r : entryInt.getValue()) {
                String[] split = r.split(":");
                Rule rule = Rule.valueOf(split[0]);
                predicateList.add(rule.function.apply(r, this, entryInt.getKey()));
            }
            ruleMap.put(entryInt.getKey(), predicateList);
        }
    }

    public void validate() {
        for (var rulesEntry : ruleMap.entrySet()) {
            for (var ruleCon : rulesEntry.getValue()) {
                ruleCon.accept(author.getADocument());
            }
        }
    }

    private Consumer<ADocument> citationsWithYears(int clauseNum, int lastYears) {
        return (document) -> {
            if (!document.hasClause(clauseNum))
                return;
            for (var citation : document.getClause(clauseNum).getSubClauses()) {

                if (!citation.checkIfCitForThePastNYears(lastYears,
                        locText.getProperty("warnings.citation.dateNotFound"))) {
                    citation.setPassed(false);
                    citation.addWarning(locText.getProperty("warnings.citation.years").formatted(lastYears));
                    continue;
                }

                singleCitation(citation);
            }
        };
    }

    private Consumer<ADocument> citations(int clauseNum) {
        return (document) -> {
            if (!document.hasClause(clauseNum))
                return;
            for (var citation : document.getClause(clauseNum).getSubClauses()) {
                singleCitation(citation);
            }
        };
    }

    private void singleCitation(ADocument.SubClause citation) {
        BiblioItem itemBibIt = citation.getProcessed();

        boolean result = author.checkIfScopusHasWork(itemBibIt);

        if (!result) {
            citation.addWarning(locText.getProperty("warnings.citation.scopus"));

            Set<String> specialtiesFound =
                    registerProcessor.getSpecialtiesFromRegister(author.getPublication(citation));
            result = specialtiesToCheckFor.stream().anyMatch(specialtiesFound::contains);

            if (!result) {
                citation.setPassed(false);
                citation.addWarning(locText.getProperty("warnings.citation.publication"));
            }
        }
    }

    private Consumer<ADocument> minimum(int clauseNum, int minimum) {
        return (document) -> {
            if (!document.hasClause(clauseNum))
                return;
            ADocument.Clause clause = document.getClause(clauseNum);

            boolean result = clause.getSubClauses().size() >= minimum;

            if (!result) {
                clause.setPassed(false);
                clause.addWarning(locText.getProperty("warnings.minimum").formatted(minimum));
            }
        };
    }

    private Consumer<ADocument> required(int clauseNum) {
        return (document) -> {
            boolean result = document.hasClause(clauseNum);

            if (!result) {
                document.setPassed(false);
                document.addWarning(locText.getProperty("warnings.required").formatted(clauseNum));
            }
        };
    }

    @FunctionalInterface
    public interface CheckedTriFunction<D, I, C, R> {
        R apply(D d, I i, C c) throws Exception;
    }

    /**
     * @author Ihor Sytnik
     */
    @AllArgsConstructor
    public enum Rule {
        req(
                (str, rulesImpl, clauseNumber) -> {
                    return rulesImpl.required(clauseNumber);
                }
        ),
        cit(
                (str, rulesImpl, clauseNumber) -> {
                    Matcher matcher = Pattern.compile("cit:L(\\d)y").matcher(str);
                    rulesImpl.citationNums.add(clauseNumber);
                    if (matcher.find())
                        return rulesImpl.citationsWithYears(clauseNumber, Integer.parseInt(matcher.group(1)));
                    return rulesImpl.citations(clauseNumber);
                }
        ),
        min(
                (str, rulesImpl, clauseNumber) -> {
                    Matcher matcher = Pattern.compile("min:(\\d)").matcher(str);
                    if (!matcher.find())
                        throw new WrongRuleSyntaxException("Couldn't parse the min rule.");

                    return rulesImpl.minimum(clauseNumber, Integer.parseInt(matcher.group(1)));
                }
        ),
    //    date(Scope.SUB_CLAUSE),
    //    hasLink(Scope.SUB_CLAUSE),
        ;

        public final CheckedTriFunction<String, RulesImplementation, Integer, Consumer<ADocument>> function;

    }
}
