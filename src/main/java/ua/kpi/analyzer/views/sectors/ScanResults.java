package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.spring.annotation.SpringComponent;
import ua.kpi.analyzer.enums.Resource;
import ua.kpi.analyzer.things.ADocument;
import ua.kpi.analyzer.things.Author;
import ua.kpi.analyzer.views.elements.ClauseVirtualListBasic;
import ua.kpi.analyzer.views.elements.SubClauseVirtualListBasic;

import java.util.Collection;
import java.util.List;

/**
 * @author Ihor Sytnik
 */
@SpringComponent
public class ScanResults extends VerticalLayout {

    private final Span processing;
    private final Div authorDetailsHeader = new Div();
    private final Span scopusAuthorId;
    private final Span orcidAuthorId;
    private final Div documentWarningsHeader = new Div();
    private Div docWarnings = new Div();
    private final Div citationInformationHeader = new Div();
    private final Span citationsFound;
    private final Span citationsPassed;
    private final SubClauseVirtualListBasic citationsPassedVirtualListBasic;
    private final Span citationsNotPassed;
    private final SubClauseVirtualListBasic citationsNotPassedVirtualListBasic;
    private final Div clausesProcResHeader = new Div();
    private final ClauseVirtualListBasic clausesList;

    private final String citationsFoundText = "Citations found: ";
    private final String citationsPassedText = "Citations passed: ";
    private final String citationsNotPassedText = "Citations that didn't pass: ";

    public ScanResults() {
        processing = new Span("Processing...");
        processing.setId("processing-indicator");

        authorDetailsHeader.getElement().appendChild(ElementFactory.createHeading3("Author Details"));
        scopusAuthorId = new Span();
        orcidAuthorId = new Span();

        documentWarningsHeader.getElement().appendChild(ElementFactory.createHeading3("Document warnings"));

        citationInformationHeader.getElement().appendChild(
                ElementFactory.createHeading3("Citation information"));
        citationsFound = new Span(citationsFoundText);
        citationsPassed = new Span(citationsPassedText);
        citationsPassedVirtualListBasic = new SubClauseVirtualListBasic();
        citationsNotPassed = new Span(citationsNotPassedText);
        citationsNotPassedVirtualListBasic = new SubClauseVirtualListBasic();

        clausesProcResHeader.getElement().appendChild(
                ElementFactory.createHeading3("Clauses processing results"));
        clausesList = new ClauseVirtualListBasic();

        citationsPassedVirtualListBasic.setWidth(50, Unit.EM);
        citationsPassedVirtualListBasic.setMaxWidth(100, Unit.PERCENTAGE);
        citationsNotPassedVirtualListBasic.setWidth(50, Unit.EM);
        citationsNotPassedVirtualListBasic.setMaxWidth(100, Unit.PERCENTAGE);
        clausesList.setWidth(50, Unit.EM);
        clausesList.setMaxWidth(100, Unit.PERCENTAGE);

        add(
                processing,
                authorDetailsHeader,
                scopusAuthorId,
                orcidAuthorId,
                documentWarningsHeader,
                citationInformationHeader,
                citationsFound,
                new Hr(),
                citationsPassed,
                citationsPassedVirtualListBasic,
                new Hr(),
                citationsNotPassed,
                citationsNotPassedVirtualListBasic,
                clausesProcResHeader,
                clausesList
        );

        setVisibleToResults(false);
        setVisibleToProcessing(false);
    }



    public void initializeResults(Author author) {
        getUI().ifPresent(ui -> ui.access(() -> {
            setVisibleToProcessing(false);
            setVisibleToResults(true);

            changeScopusAuthorId(
                    author.getIdentifier(Resource.SCOPUS) != null &&
                    !author.getIdentifier(Resource.SCOPUS).isBlank() ?
                        author.getIdentifier(Resource.SCOPUS) :
                        "NOT FOUND");
            changeOrcidAuthorId(
                    author.getIdentifier(Resource.ORCID) != null &&
                    !author.getIdentifier(Resource.ORCID).isBlank() ?
                        author.getIdentifier(Resource.ORCID) :
                        "NOT FOUND");

            changeCitationsFoundText(
                    String.valueOf(author.getADocument().getCitations().size()));
            changeCitationsPassedText(
                    String.valueOf(author.getADocument().getPassedCitations().size()));
            changeCitationsNotPassedText(
                    String.valueOf(author.getADocument().getNonPassedCitations().size()));


            docWarnings.removeFromParent();
            List<String> warnings = author.getADocument().getWarnings();
            if (!warnings.isEmpty()) {
                docWarnings = new Div();
                for (var warn : warnings) {
                    docWarnings.add(new Text(warn));
                }
                addComponentAtIndex(5, docWarnings);
            } else {
                docWarnings.setVisible(false);
            }

            changeClausesList(author.getADocument().getClauses().values());
            changeCitationsPassed(author.getADocument().getPassedCitations());
            changeCitationsNotPassed(author.getADocument().getNonPassedCitations());
        }));

    }

    private void changeScopusAuthorId(String str) {
        scopusAuthorId.setText("Scopus author id: " + str);
    }

    private void changeOrcidAuthorId(String str) {
        orcidAuthorId.setText("Orcid author id: " + str);
    }

    private void changeCitationsFoundText(String str) {
        citationsFound.setText(citationsFoundText + str);
    }

    private void changeCitationsPassedText(String str) {
        citationsPassed.setText(citationsPassedText + str);
    }

    private void changeCitationsNotPassedText(String str) {
        citationsNotPassed.setText(citationsNotPassedText + str);
    }

    private void changeClausesList(Collection<ADocument.Clause> clauseCollection) {
        clausesList.initialize(clauseCollection);
    }
    private void changeCitationsPassed(Collection<ADocument.SubClause> subClauseCollection) {
        citationsPassedVirtualListBasic.initialize(subClauseCollection);
    }
    private void changeCitationsNotPassed(Collection<ADocument.SubClause> subClauseCollection) {
        citationsNotPassedVirtualListBasic.initialize(subClauseCollection);
    }

    public void setVisibleToResults(boolean visible) {
        getChildren()
                .filter(component -> !component.equals(processing))
                .forEach(component -> component.setVisible(visible));
    }

    public void setVisibleToProcessing(boolean visible) {
        processing.setVisible(visible);
    }

}
