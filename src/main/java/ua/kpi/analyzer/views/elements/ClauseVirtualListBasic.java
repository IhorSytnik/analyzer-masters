package ua.kpi.analyzer.views.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.ElementFactory;
import ua.kpi.analyzer.things.ADocument;
import ua.kpi.analyzer.views.Helping;

import java.util.Collection;
import java.util.List;


public class ClauseVirtualListBasic extends Div {

    private final ComponentRenderer<Component, ADocument.Clause> clauseCardRenderer = new ComponentRenderer<>(
            clause -> {
                VerticalLayout cardLayout = new VerticalLayout();
//                cardLayout.setMargin(true);

                VerticalLayout infoLayout = new VerticalLayout();
//                infoLayout.setSpacing(false);
//                infoLayout.setPadding(false);
                infoLayout.getElement().appendChild(
                        ElementFactory.createStrong("Clause №%d - %s".formatted(
                                clause.getClauseNumber(),
                                clause.isPassed() ? "passed" : "didn't pass"
                        )));

                /*Clause warnings*/
                if (!clause.getWarnings().isEmpty()) {
                    Helping.addWarningDetails(infoLayout, clause, "Clause warnings");
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
                                sc,
                                "Subclause warnings");
                    }
                    infoLayout.add(new Div(subClauseLayout));
                }

                cardLayout.add(infoLayout, new Hr());

                return cardLayout;
            });

    public void initialize(Collection<ADocument.Clause> clauseList) {
        removeAll();
        VirtualList<ADocument.Clause> list = new VirtualList<>();
        list.setItems(clauseList);
        list.setRenderer(clauseCardRenderer);
        add(list);
    }

}
