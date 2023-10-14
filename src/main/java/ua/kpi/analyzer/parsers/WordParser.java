package ua.kpi.analyzer.parsers;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.entities.ADocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Component
public class WordParser {

    @Getter
    @Setter
    private ADocument aDocument;
    @Autowired
    private CitationParser citationParser;
    @Autowired
    private Set<Integer> citationNums;

    public void setParagraphs(InputStream inputStream) throws IOException {
        XWPFDocument document = new XWPFDocument(inputStream);

        List<ADocument.Paragraph> paragraphList = new ArrayList<>();
        int lineNum = 0;
        for (var para : document.getParagraphs()) {
            paragraphList.add(new ADocument.Paragraph(lineNum++, para));
        }
        aDocument.setParagraphs(paragraphList);
        document.close();

        setClauses();
    }

    private void setClauses() {
        Pattern clausesPattern = Pattern.compile("^\\s*[пП]\\s*\\.?\\s*(\\d+)[^\\d]?\\s*");
        String subclausesPatternString = "^\\s*%d\\s*\\.\\s*\\d+";
        Function<Integer, Pattern> subclausesPatternFunction = (c) ->
                Pattern.compile(subclausesPatternString.formatted(c));

        var parIterator = aDocument.getParagraphs().iterator();
        Matcher clauseMatcher = clausesPattern.matcher(parIterator.next().getText());
        int clauseNumber;

        ADocument.Paragraph prev = null;

        while (parIterator.hasNext()) {
            if (clauseMatcher.matches()) {

                clauseNumber = Integer.parseInt(clauseMatcher.group(1));
                if (citationNums.contains(clauseNumber)) {

                    boolean newPrev = false;

                    do {
                        var par1 = newPrev ? prev : parIterator.next();
                        newPrev = false;
                        clauseMatcher.reset(par1.getText());
                        if (clauseMatcher.matches()) {

                            clauseNumber = Integer.parseInt(clauseMatcher.group(1));
                            continue;
                        } else if (Pattern.compile("^\\s*$").matcher(par1.getText()).matches()) {
                            continue;
                        }
                        ADocument.SubClause subClause = new ADocument.SubClause();

                        subClause.getParagraphs().add(par1);

                        while (parIterator.hasNext()) {
                            var par2 = parIterator.next();
                            clauseMatcher.reset(par2.getText());

                            if (clauseMatcher.matches() ||
                                    subclausesPatternFunction.apply(clauseNumber)
                                    .matcher(par2.getText())
                                    .find()) {
                                prev = par2;
                                newPrev = true;
                                break;
                            } else if (Pattern.compile("^\\s*$").matcher(par2.getText()).matches()) {
                                continue;
                            }
                            subClause.getParagraphs().add(par2);

                        }

                        boolean isCitation = false;
                        if (citationNums.contains(clauseNumber)) {
                            processCitation(subClause);
                            isCitation = true;
                        }

                        aDocument.addSubClause(clauseNumber, subClause, isCitation);

                    } while (parIterator.hasNext());

                } else if (parIterator.hasNext()) {
                    clauseMatcher.reset(parIterator.next().getText());
                }
            } else {
                clauseMatcher.reset(parIterator.next().getText());
            }
        }
    }

    public String getScopusAuthorId() {
        Pattern scopusAuthorPattern = Pattern.compile("scopus\\.com/authid/detail\\.uri\\?authorId=(\\d{11})");
        for (var para : aDocument.getParagraphs()) {
            Matcher matcher = scopusAuthorPattern.matcher(para.getText());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    private void processCitation(ADocument.SubClause subClause) {

        subClause.setCitation(true);
        StringBuilder stringBuilder = new StringBuilder();
        for (var para : subClause.getParagraphs()) {
            stringBuilder.append(para.getText());
        }

        subClause.setProcessed(citationParser.processCitation(stringBuilder.toString()));
    }
}
