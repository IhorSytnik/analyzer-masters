package ua.kpi.analyzer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.enums.Resource;
import ua.kpi.analyzer.parsers.RulesImplementation;
import ua.kpi.analyzer.parsers.WordParser;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.requests.OrcidFinder;
import ua.kpi.analyzer.requests.ScopusFinder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ihor Sytnik
 */
@Component
@Getter
public class Processor {

    @Autowired
    private WordParser wordParser;
    @Autowired
    private OrcidFinder orcidFinder;
    @Autowired
    private ScopusFinder scopusFinder;
    @Autowired
    private RulesImplementation rulesImplementation;

    private final Author author = new Author();

    public void process(InputStream inputStream)
            throws IOException, InterruptedException {
        initialize();

        wordParser.setADocument(author.getADocument());
        wordParser.setParagraphs(inputStream);

        author.addIdentifier(Resource.SCOPUS, wordParser.getScopusAuthorId());
        author.addIdentifier(Resource.ORCID,
                orcidFinder.findAuthorsOrcid(author, author.getADocument().getCitations()));

        if (!author.getIdentifier(Resource.SCOPUS).isBlank()) {
            author.setScopusWorks(scopusFinder.
                    getAuthorWorks(author.getIdentifier(Resource.SCOPUS)));
        }

        rulesImplementation.setAuthor(author);
        rulesImplementation.validate();

    }

    public String getAuthorsId(Resource resource) {
        return author.getIdentifier(resource);
    }

    private void initialize() {
        author.initialize();
    }
}
