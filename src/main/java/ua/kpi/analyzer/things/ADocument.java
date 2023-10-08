package ua.kpi.analyzer.things;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.grobid.core.data.BiblioItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
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
    @JsonSerialize(using = Paragraph.ParagraphSerializer.class)
    public static class Paragraph {
        private int lineNumber;
        private XWPFParagraph textObject;

        public String getText() {
            return textObject.getText();
        }

//        @Component
        public static class ParagraphSerializer extends StdSerializer<Paragraph> {

            public ParagraphSerializer() {
                this(null);
            }

            private ParagraphSerializer(Class<Paragraph> p) {
                super(p);
            }

            @Override
            public void serialize(
                    Paragraph value, JsonGenerator gen, SerializerProvider provider
            ) throws IOException {
                gen.writeStartObject();
                gen.writeNumberField("lineNumber", value.lineNumber);
                gen.writeStringField("text", value.getText());
                gen.writeEndObject();
            }
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

        public int getLineNumberFirst() {
            return paragraphs.get(0).getLineNumber();
        }

        public int getLineNumberLast() {
            return paragraphs.get(paragraphs.size() - 1).getLineNumber();
        }

        @Override
        public void addWarning(String str) {
            warnings.add(str);
        }

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
