package ua.kpi.analyzer.parsers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.grobid.core.data.BiblioItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
    @Autowired
    private Set<String> specialtiesToCheckFor;

    Set<Integer> citationNums;

    public RulesImplementation(
            @Autowired Set<Integer> citationNums,
            @Value("#{${clauses.rules}}") Map<Integer, List<String>> clauseRules) throws Exception {

        this.citationNums = citationNums;

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

    private Consumer<ADocument> citation(int clauseNum) {
        return (document) -> {
            if (!document.hasClause(clauseNum))
                return;
            for (var citation : document.getClause(clauseNum).getSubClauses()) {
                BiblioItem itemBibIt = citation.getProcessed();

                boolean result = author.checkIfScopusHasWork(itemBibIt);

                if (!result) {
                    citation.addWarning("This work wasn't found on Scopus.");

                    try {
                        Set<String> specialtiesFound =
                                registerProcessor.getSpecialtiesFromRegister(author.getPublication(citation));
                        result = specialtiesToCheckFor.stream().anyMatch(specialtiesFound::contains);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!result) {
                        citation.setPassed(false);
                        citation.addWarning("The publication didn't pass the requirements " +
                                "(the publication doesn't have proper specialties associated with it).");
                    }
                }
            }
        };
    }

    private Consumer<ADocument> minimum(int clauseNum, int minimum) {
        return (document) -> {
            ADocument.Clause clause = document.getClause(clauseNum);

            boolean result = clause.getSubClauses().size() >= minimum;

            if (!result) {
                clause.setPassed(false);
                clause.addWarning("This clause should have %d items at minimum".formatted(minimum));
            }
        };
    }

    private Consumer<ADocument> required(int clauseNum) {
        return (document) -> {
            boolean result = document.hasClause(clauseNum);

            if (!result) {
                document.setPassed(false);
                document.addWarning("Clause %d is required".formatted(clauseNum));
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
                    rulesImpl.citationNums.add(clauseNumber);
                    return rulesImpl.citation(clauseNumber);
                }
        ),
        min(
                (str, rulesImpl, clauseNumber) -> {
                    Matcher matcher = Pattern.compile("min:(\\d)").matcher(str);
                    if (!matcher.find())
                        throw new Exception("Couldn't parse the min rule.");

                    return rulesImpl.minimum(clauseNumber, Integer.parseInt(matcher.group(1)));
                }
        ),
    //    date(Scope.SUB_CLAUSE),
    //    hasLink(Scope.SUB_CLAUSE),
        ;

        public final CheckedTriFunction<String, RulesImplementation, Integer, Consumer<ADocument>> function;

    }
}
