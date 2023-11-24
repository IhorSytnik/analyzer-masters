package ua.kpi.analyzer.views.elements;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.analyzer.entities.ADocument;
import ua.kpi.analyzer.views.Helping;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

@AllArgsConstructor
public class ClauseVirtualListBasic extends Div {

    @Autowired
    private Properties locText;

    private VerticalLayout clauseCardRenderer(ADocument.Clause clause) {
        VerticalLayout cardLayout = new VerticalLayout();
//                cardLayout.setMargin(true);

        VerticalLayout infoLayout = new VerticalLayout();
//                infoLayout.setSpacing(false);
//                infoLayout.setPadding(false);
        infoLayout.getElement().appendChild(
                ElementFactory.createStrong(locText.getProperty("ui.results.clauseList.header").formatted(
                        clause.getClauseNumber(),
                        clause.isPassed() ?
                                locText.getProperty("ui.results.clauseList.passed") :
                                locText.getProperty("ui.results.clauseList.notPassed")
                )));

        /*Clause warnings*/
        if (!clause.getWarnings().isEmpty()) {
            Helping.addWarningDetails(infoLayout, clause);
        }

        /*Subclauses*/
        List<ADocument.SubClause> subClauses = clause.getNonPassedNonCitations();
        if (!subClauses.isEmpty()) {

            VerticalLayout subClauseLayout = new VerticalLayout();
            for (var sc : subClauses) {
                subClauseLayout.add(new Div(new Text(sc.getText())));
                /*Subclause warnings*/
                Helping.addWarningDetails(
                        subClauseLayout,
                        sc
                );
            }
            infoLayout.add(new Div(subClauseLayout));
        }

        cardLayout.add(infoLayout, new Hr());

        return cardLayout;
    }

    public void initialize(Collection<ADocument.Clause> clauseList) {
        removeAll();
        VerticalLayout list = new VerticalLayout();
        for (var clause : clauseList) {
            list.add(clauseCardRenderer(clause));
        }
        add(list);
    }

}
