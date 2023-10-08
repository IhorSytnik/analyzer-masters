package ua.kpi.analyzer.parsers;

import org.grobid.core.data.BiblioItem;
import org.grobid.core.engines.Engine;
import org.grobid.core.lang.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Sytnik
 */
@Component
public class GrobidParser implements CitationParser {

    @Autowired
    private Engine grobidEngine;

    @Override
    public List<BiblioItem> processCitations(List<String> citations) {
        List<BiblioItem> biblioItemList = new ArrayList<>();

        for (var cit : citations) {
            biblioItemList.add(grobidEngine.processRawReference(cit, 1));
        }

        return biblioItemList;
    }

    @Override
    public BiblioItem processCitation(String citation) {
        return grobidEngine.processRawReference(citation, 1);
    }

    public Language runLanguageId(String filePath) {
        return grobidEngine.runLanguageId(filePath, "pdf");
    }
}
