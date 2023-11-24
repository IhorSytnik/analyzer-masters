package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ua.kpi.analyzer.enums.Resource;
import ua.kpi.analyzer.entities.ADocument;
import ua.kpi.analyzer.entities.Author;
import ua.kpi.analyzer.views.elements.ClauseVirtualListBasic;
import ua.kpi.analyzer.views.elements.SubClauseVirtualListBasic;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author Ihor Sytnik
 */
@SpringComponent
public class ScanResults extends VerticalLayout {

    private final String warningBgColor;
    private final String warningFontColor;

    private final Span processing;
    private final Div authorDetailsHeader = new Div();
    private final Span scopusAuthorId;
    private final Span orcidAuthorId;
    private final Div documentWarningsHeader = new Div();
    private Div docWarnings = new Div();
    private final Div citationInformationHeader = new Div();
    private final Div citationsFound = new Div();
    private final Div citationsPassed = new Div();
    private final SubClauseVirtualListBasic citationsPassedVirtualListBasic;
    private final Div citationsNotPassed = new Div();
    private final SubClauseVirtualListBasic citationsNotPassedVirtualListBasic;
    private final Div clausesProcResHeader = new Div();
    private final ClauseVirtualListBasic clausesList;
    private Properties locText;

    public ScanResults(
            @Value("${warning.background.color}") String warningBgColor,
            @Value("${warning.font.color}") String warningFontColor,
            @Autowired Properties locText) {

        this.locText = locText;
        this.warningBgColor = warningBgColor;
        this.warningFontColor = warningFontColor;

        processing = new Span(locText.getProperty("ui.results.processing"));
        processing.setId("processing-indicator");

        authorDetailsHeader.getElement().appendChild(ElementFactory.createHeading3(
                locText.getProperty("ui.results.authorDetailsHeader")));
        scopusAuthorId = new Span();
        orcidAuthorId = new Span();

        documentWarningsHeader.getElement().appendChild(ElementFactory.createHeading3(
                locText.getProperty("ui.results.documentWarningsHeader")));

        citationInformationHeader.getElement().appendChild(
                ElementFactory.createHeading3(locText.getProperty("ui.results.citationInformationHeader")));
        citationsPassedVirtualListBasic = new SubClauseVirtualListBasic();
        citationsNotPassedVirtualListBasic = new SubClauseVirtualListBasic();

        clausesProcResHeader.getElement().appendChild(
                ElementFactory.createHeading3(locText.getProperty("ui.results.clausesProcResHeader")));
        clausesList = new ClauseVirtualListBasic(locText);

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
                            locText.getProperty("ui.results.idNotFound"));
            changeOrcidAuthorId(
                    author.getIdentifier(Resource.ORCID) != null &&
                            !author.getIdentifier(Resource.ORCID).isBlank() ?
                            author.getIdentifier(Resource.ORCID) :
                            locText.getProperty("ui.results.idNotFound"));

            changeCitationsFoundText(
                    String.valueOf(author.getADocument().getCitations().size()));
            changeCitationsPassedText(
                    String.valueOf(author.getADocument().getPassedCitations().size()));
            changeCitationsNotPassedText(
                    String.valueOf(author.getADocument().getNonPassedCitations().size()));


            docWarnings.removeFromParent();
            List<String> warnings = author.getADocument().getWarnings();
            if (!warnings.isEmpty()) {
                VerticalLayout docWarningLayout = new VerticalLayout();
                for (var warn : warnings) {
                    docWarningLayout.add(new Div(new Text(warn)));
                }
                docWarnings = new Div(docWarningLayout);
                docWarnings.setWidth(30, Unit.EM);
                docWarnings.setMaxWidth(100, Unit.PERCENTAGE);
                docWarnings.getStyle()
                        .set("background-color", warningBgColor)
                        .set("color", warningFontColor);
                addComponentAtIndex(5, docWarnings);
            }
//            else {
//                docWarnings.setVisible(false);
//            }

            changeClausesList(author.getADocument().getClauses().values());
            changeCitationsPassed(author.getADocument().getPassedCitations());
            changeCitationsNotPassed(author.getADocument().getNonPassedCitations());
        }));

    }

    private void changeScopusAuthorId(String str) {
        scopusAuthorId.setText(locText.getProperty("ui.results.scopusAuthorId") + ": " + str);
    }

    private void changeOrcidAuthorId(String str) {
        orcidAuthorId.setText(locText.getProperty("ui.results.orcidAuthorId") + ": " + str);
    }

    private void changeCitationsFoundText(String str) {
        citationsFound.getElement().removeAllChildren();
        citationsFound.getElement().appendChild(
                ElementFactory.createHeading4(locText.getProperty("ui.results.citationsFoundText") +
                        ": " + str));
    }

    private void changeCitationsPassedText(String str) {
        citationsPassed.getElement().removeAllChildren();
        citationsPassed.getElement().appendChild(
                ElementFactory.createHeading4(locText.getProperty("ui.results.citationsPassedText") +
                        ": " + str));
    }

    private void changeCitationsNotPassedText(String str) {
        citationsNotPassed.getElement().removeAllChildren();
        citationsNotPassed.getElement().appendChild(
                ElementFactory.createHeading4(locText.getProperty("ui.results.citationsNotPassedText") +
                        ": " + str));
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
