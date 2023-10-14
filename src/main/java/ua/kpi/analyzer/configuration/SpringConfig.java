package ua.kpi.analyzer.configuration;

import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import ua.kpi.analyzer.requests.HttpRequestBrowser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ihor Sytnik
 */
@Configuration
@ComponentScan("ua.kpi.analyzer")
@PropertySource({
        "classpath:project.properties",
        "classpath:secret.properties",
        "classpath:grobid.properties"
})
public class SpringConfig {

    @Bean
    public HttpRequestBrowser orcidRequestBrowser(
            @Value("${orcid.website}") String website,
            @Value("${orcid.sleep}") int sleepMilliseconds,
            @Value("#{${orcid.headers}}") HttpHeaders headers) {
        return new HttpRequestBrowser(website, true, headers, sleepMilliseconds);
    }

    @Bean
    public HttpRequestBrowser scopusAPIRequestBrowser(
            @Value("${scopus.api.website}") String website,
            @Value("${scopus.sleep}") int sleepMilliseconds,
            @Value("#{${scopus.headers}}") HttpHeaders headers,
            @Value("#{${scopus.api.secret.headers}}") HttpHeaders scopusApiSecretHeaders) {
        HttpRequestBrowser scopusAPIRequestBrowser =
                new HttpRequestBrowser(website, true, headers, sleepMilliseconds);
        scopusAPIRequestBrowser.addHeaders(scopusApiSecretHeaders);
        return scopusAPIRequestBrowser;
    }

    @Bean
    public HttpRequestBrowser registerRequestBrowser(
            @Value("${register.website}") String website,
            @Value("${register.sleep}") int sleepMilliseconds,
            @Value("#{${register.headers}}") HttpHeaders headers) {
        return new HttpRequestBrowser(website, true, headers, sleepMilliseconds);
    }

    @Bean
    public Engine grobidEngine(@Value("${grobid.pGrobidHome}") String grobidHome) {
        GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(List.of(grobidHome));
        GrobidProperties.getInstance(grobidHomeFinder);
        return GrobidFactory.getInstance().createEngine();
    }

    @Bean
    public Set<String> specialtiesToCheckFor() {
        return new HashSet<>();
    }

    @Bean
    public Set<Integer> citationNums() {
        return new HashSet<>();
    }
}
