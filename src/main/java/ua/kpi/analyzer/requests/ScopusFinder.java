package ua.kpi.analyzer.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.entities.Work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Sytnik
 */
@Component
public class ScopusFinder {

    @Autowired
    private HttpRequestBrowser scopusAPIRequestBrowser;

    public String getSearch(String authorId, int start, int count) throws InterruptedException {
        return scopusAPIRequestBrowser
                .get(
                        "/content/search/scopus?query=au-id(%s)&start=%d&count=%d"
                                .formatted(
                                        authorId,
                                        start,
                                        count
                                )
                ).bodyToMono(String.class).block();
    }

    public List<Work> getAuthorWorks(String authorId)
            throws InterruptedException, IOException {

        List<Work> results = new ArrayList<>();
        int start = -20;
        int count = 20;
        String jsonStr;
        JsonNode worksJSON;

        do {
            start += count;

            jsonStr = getSearch(authorId, start, count);
            worksJSON = new ObjectMapper().readTree(jsonStr).get("search-results");

            for (var entry : worksJSON.get("entry")) {
                results.add(new ObjectMapper().readValue(entry.traverse(), Work.class));
            }
        } while (worksJSON.get("opensearch:totalResults").asInt() > start + count);

        return results;
    }
}
