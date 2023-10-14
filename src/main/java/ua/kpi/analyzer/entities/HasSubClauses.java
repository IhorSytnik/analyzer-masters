package ua.kpi.analyzer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Sytnik
 */
@Setter
@Getter
public abstract class HasSubClauses {

    protected final List<ADocument.SubClause> subClauses = new ArrayList<>();

    @JsonIgnore
    public List<ADocument.SubClause> getPassedSubClauses() {
        return subClauses.stream().filter(ADocument.SubClause::isPassed).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getNonPassed() {
        return subClauses.stream().filter(ADocument.SubClause::isPassed).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getCitations() {
        return subClauses.stream().filter(ADocument.SubClause::isCitation).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getPassedCitations() {
        return getCitations().stream().filter(ADocument.SubClause::isPassed).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getNonPassedCitations() {
        return getCitations().stream().filter(subClause -> !subClause.isPassed()).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getNonPassedNonCitations() {
        return getNonCitations().stream().filter(subClause -> !subClause.isPassed()).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getNonCitations() {
        return subClauses.stream().filter(subClause -> !subClause.isCitation()).toList();
    }

    @JsonIgnore
    public List<ADocument.SubClause> getNonPassedSubClauses() {
        return subClauses.stream().filter(subClause -> !subClause.isPassed()).toList();
    }

}
