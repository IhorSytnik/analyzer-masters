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
import java.util.Set;

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

    public void process(InputStream inputStream, Set<String> specialtiesToCheckFor)
            throws IOException {
        initialize();

        wordParser.setADocument(author.getADocument());
        wordParser.setParagraphs(inputStream);

        author.addIdentifier(Resource.SCOPUS, wordParser.getScopusAuthorId());
        author.setPosition(wordParser.getPosition());
        author.setQualification(wordParser.getQualification());
        author.setExperienceInYears(wordParser.getExperienceInYears());
        author.addIdentifier(Resource.ORCID,
                orcidFinder.findAuthorsOrcid(author, author.getADocument().getCitations()));

        if (!author.getIdentifier(Resource.SCOPUS).isBlank()) {
            author.setScopusWorks(scopusFinder.
                    getAuthorWorks(author.getIdentifier(Resource.SCOPUS)));
        }

        rulesImplementation.setSpecialtiesToCheckFor(specialtiesToCheckFor);
        rulesImplementation.setAuthor(author);
        rulesImplementation.validate();
    }

    private void initialize() {
        author.initialize();
    }
}
