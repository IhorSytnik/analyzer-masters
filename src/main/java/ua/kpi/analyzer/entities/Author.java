package ua.kpi.analyzer.entities;

import lombok.Getter;
import lombok.Setter;
import org.grobid.core.data.BiblioItem;
import ua.kpi.analyzer.enums.Resource;

import java.util.*;

/**
 * @author Ihor Sytnik
 */
@Getter
@Setter
public class Author {
    private Map<Resource, String> identifiers = new HashMap<>();
    private Set<String> orcidWorkIdSet = new HashSet<>();
    private List<Work> scopusWorks = new ArrayList<>();
    private ADocument aDocument = new ADocument();

    public String addIdentifier(Resource resource, String idStr) {
        return identifiers.put(resource, idStr);
    }

    public String getIdentifier(Resource resource) {
        return identifiers.get(resource);
    }

    public List<Object> filterOutWorksOrcid() {

        List<Object> citList = new ArrayList<>();
        for (var cit : aDocument.getSubClauses()) {
            BiblioItem itemBibIt = cit.getProcessed();

            List<String> idList = new ArrayList<>();
            Collections.addAll(idList,
                    itemBibIt.getDOI(),
                    itemBibIt.getArXivId(),
                    itemBibIt.getPMID(),
                    itemBibIt.getPMCID(),
                    itemBibIt.getPII(),
                    itemBibIt.getArk(),
                    itemBibIt.getIstexId(),
                    itemBibIt.getPubnum()
            );
            if (Collections.disjoint(orcidWorkIdSet,
                    idList)) {
                citList.add(cit);
            }
        }

        return citList;
    }

    public boolean checkIfScopusHasWork(BiblioItem itemBibIt) {

        for (var scopusWork : scopusWorks) {
            if (itemBibIt.getDOI() != null &&
                    scopusWork.getDoi() != null &&
                    itemBibIt.getDOI().equals(scopusWork.getDoi())) {
                return true;
            }
        }

        return false;
    }

    public Publication getPublication(ADocument.SubClause work) {

        BiblioItem itemBibIt = work.getProcessed();

        Publication publication = new Publication();

        if (itemBibIt.getISSN() != null) {
            publication.setIssn(itemBibIt.getISSN());
        }
        if (itemBibIt.getISSNe() != null) {
            publication.setEissn(itemBibIt.getISSNe());
        }
        if (itemBibIt.getPublisher() != null) {
            publication.setPublicationName(itemBibIt.getPublisher());
        }

        return publication;
    }

    public void initialize() {
        identifiers = new HashMap<>();
        orcidWorkIdSet = new HashSet<>();
        scopusWorks = new ArrayList<>();
        aDocument = new ADocument();
    }
}
