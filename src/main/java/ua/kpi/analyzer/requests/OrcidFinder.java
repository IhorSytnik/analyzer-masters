package ua.kpi.analyzer.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.grobid.core.data.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.entities.ADocument;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.enums.Resource;

import java.util.*;

/**
 * @author Ihor Sytnik
 */
@Component
public class OrcidFinder {

    @Autowired
    private HttpRequestBrowser orcidRequestBrowser;

    private String getPublicRecord(String orcid) {
        return orcidRequestBrowser
                .get(
                        "/%s/public-record.json".formatted(orcid)
                ).bodyToMono(String.class).block();
    }

    private String getWorksExtendedPage(String orcid, int offset, int pageSize) {
        return orcidRequestBrowser
                .get(
                        "/%s/worksExtendedPage.json?offset=%d&sort=date&sortAsc=false&pageSize=%d"
                                .formatted(
                                        orcid,
                                        offset,
                                        pageSize)
                ).bodyToMono(String.class).block();

    }

    private Map<Resource, String> getExternalIdentifiers(String orcid)
            throws JsonProcessingException {

        Map<Resource, String> results = new HashMap<>();
        String jsonStr = getPublicRecord(orcid);
        JsonNode publicRecordJSON = new ObjectMapper().readTree(jsonStr);

        for (var exIds : publicRecordJSON.get("externalIdentifier").get("externalIdentifiers")) {
            results.put(Resource.getSimilar(exIds.get("sourceName").asText()), exIds.get("reference").asText());
        }

        return results;
    }

    private boolean identify(String orcid, Author author)
            throws JsonProcessingException {

        for (var exIdsEntry : getExternalIdentifiers(orcid).entrySet()) {
            for (var foundIdsEntry : author.getIdentifiers().entrySet()) {
                if (foundIdsEntry.getValue().equals(exIdsEntry.getValue()) &&
                        foundIdsEntry.getKey() == exIdsEntry.getKey()) {
                    return true;
                }
            }
        }

        return false;
    }

    public String findAuthorsOrcid(
            Author author, List<ADocument.SubClause> processedSubClauses)
            throws JsonProcessingException {

        for (var bib : processedSubClauses) {
            List<Person> personList = bib.getProcessed().getFullAuthors();
            if (personList != null) {
                for (var person : personList) {
                    String orcid = person.getORCID();
                    if (person.getORCID() != null && identify(orcid, author)) {
                        return orcid;
                    }
                }
            }
        }
        return "";
    }

    public Set<String> getAllWorks(String orcid)
            throws JsonProcessingException {

        Set<String> resultSet = new HashSet<>();

        int offset = 0;
        int pageSize = 50;
        String jsonStr = getWorksExtendedPage(orcid, offset, pageSize);
        JsonNode worksExtendedPageJSON = new ObjectMapper().readTree(jsonStr);
        int totalGroups = Integer.parseInt(worksExtendedPageJSON.get("totalGroups").asText());

        for (var group : worksExtendedPageJSON.get("groups")) {
            for (var work : group.get("works")) {
                for (var workExternalIdentifier : work.get("workExternalIdentifiers")) {
                    resultSet.add(workExternalIdentifier
                            .get("externalIdentifierId")
                            .get("value").asText());
                }
            }
        }

        while (pageSize + offset < totalGroups) {
            offset += pageSize;
            jsonStr = getWorksExtendedPage(orcid, offset, pageSize);
            worksExtendedPageJSON = new ObjectMapper().readTree(jsonStr);

            for (var group : worksExtendedPageJSON.get("groups")) {
                for (var work : group.get("works")) {
                    for (var workExternalIdentifier : work.get("workExternalIdentifiers")) {
                        resultSet.add(workExternalIdentifier
                                .get("externalIdentifierId")
                                .get("value").asText());
                    }
                }
            }
        }

        return resultSet;
    }

}
