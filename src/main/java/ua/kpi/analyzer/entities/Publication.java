package ua.kpi.analyzer.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Publication {
    @JsonProperty("prism:issn")
    private String issn;
    @JsonProperty("prism:eIssn")
    private String eissn;
    @JsonProperty("prism:publicationName")
    private String publicationName;

    public void setEissn(String eissn) {
        Pattern patternDash = Pattern.compile("\\d{4}-\\d{4}");
        Pattern patternNoDash = Pattern.compile("\\d{8}");

        if (patternDash.asMatchPredicate().test(eissn)) {
            this.eissn = eissn;
        } else if (patternNoDash.asMatchPredicate().test(eissn)) {
            this.eissn = eissn.substring(0, 4) + '-' + eissn.substring(4);
        }
    }

    public void setIssn(String issn) {
        Pattern patternDash = Pattern.compile("\\d{4}-\\d{4}");
        Pattern patternNoDash = Pattern.compile("\\d{8}");

        if (patternDash.asMatchPredicate().test(issn)) {
            this.issn = issn;
        } else if (patternNoDash.asMatchPredicate().test(issn)) {
            this.issn = issn.substring(0, 4) + '-' + issn.substring(4);
        }
    }
}
