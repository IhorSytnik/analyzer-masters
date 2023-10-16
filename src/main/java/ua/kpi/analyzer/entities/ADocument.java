package ua.kpi.analyzer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.grobid.core.data.BiblioItem;
import org.grobid.core.data.Date;
import ua.kpi.analyzer.exceptions.IsNotACitationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
@JsonIgnoreProperties({"subClauses"})
public class ADocument extends HasSubClauses implements ClassWithWarnings {
    private String path;
    private List<Paragraph> paragraphs;
    private Map<Integer, Clause> clauses = new HashMap<>();
    private final List<String> warnings = new ArrayList<>();
    private boolean isPassed = true;

    public void addSubClause(int clauseNumber, SubClause subClause, boolean isCitation) {
        subClause.setClauseNumber(clauseNumber);
        subClauses.add(subClause);

        if (!clauses.containsKey(clauseNumber)) {
            clauses.put(clauseNumber, new Clause(clauseNumber, isCitation));
        }
        clauses.get(clauseNumber).getSubClauses().add(subClause);
    }

    public Clause getClause(int clauseNumber) {
        return clauses.get(clauseNumber);
    }

    public boolean hasClause(int clauseNumber) {
        return clauses.containsKey(clauseNumber);
    }

    public List<SubClause> getSubClausesByClauseNumber(int clauseNumber) {
        return clauses.get(clauseNumber).getSubClauses();
    }

    @Override
    public void addWarning(String str) {
        warnings.add(str);
    }

    @RequiredArgsConstructor
    @Setter
    @Getter
    public static class Clause extends HasSubClauses implements ClassWithWarnings {
        private final int clauseNumber;
        private final List<String> warnings = new ArrayList<>();
        private boolean passed = true;
        private final boolean isCitation;

        @Override
        public void addWarning(String str) {
            warnings.add(str);
        }
    }

    @AllArgsConstructor
    @Setter
    @Getter
    public static class Paragraph {
        private int lineNumber;
        @JsonIgnore
        private XWPFParagraph textObject;

        public String getText() {
            return textObject.getText();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class SubClause implements ClassWithWarnings {
        private int clauseNumber;
        private List<Paragraph> paragraphs = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        private boolean passed = true;
        private boolean isCitation = false;
        @JsonIgnore
        private BiblioItem processed;

        @JsonIgnore
        public int getLineNumberFirst() {
            return paragraphs.get(0).getLineNumber();
        }

        @JsonIgnore
        public int getLineNumberLast() {
            return paragraphs.get(paragraphs.size() - 1).getLineNumber();
        }

        public boolean checkIfCitForThePastNYears(int years) {
            if (!isCitation) {
                throw new IsNotACitationException(
                        "Subclause in clause %d on lines %d-%d isn't a citation.".formatted(
                                clauseNumber,
                                getLineNumberFirst(),
                                getLineNumberLast()
                        ));
            }

            Date pubDate = processed.getNormalizedPublicationDate();

            if (pubDate != null) {
                int pubY = pubDate.getYear();

                LocalDate nYearsAgo = LocalDate.now();
                nYearsAgo = nYearsAgo.minusYears(years);

                if (pubY != -1) {
                    int pubM = pubDate.getMonth();
                    int pubD = pubDate.getDay();
                    if (pubM != -1 && pubD != -1) {
                        LocalDate publicationDate = LocalDate.of(pubY, pubM, pubD);

                        return publicationDate.compareTo(nYearsAgo) > 0;
                    }
                    return pubY > nYearsAgo.getYear();
                }
            }
            addWarning("Couldn't find publication date.");
            return true;
        }

        @Override
        public void addWarning(String str) {
            warnings.add(str);
        }

        @JsonIgnore
        public String getText() {
            StringBuilder stringBuilder = new StringBuilder();

            for (var par : paragraphs) {
                stringBuilder.append(par.getText());
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
    }
}
