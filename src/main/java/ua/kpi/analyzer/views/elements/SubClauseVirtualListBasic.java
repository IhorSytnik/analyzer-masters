package ua.kpi.analyzer.views.elements;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import ua.kpi.analyzer.entities.ADocument;
import ua.kpi.analyzer.views.Helping;

import java.util.Collection;


public class SubClauseVirtualListBasic extends Div {

    private VerticalLayout subClauseComponentRenderer(ADocument.SubClause subClause) {

        VerticalLayout infoLayout = new VerticalLayout();
//                infoLayout.setSpacing(false);
//                infoLayout.setPadding(false);
        infoLayout.getElement().appendChild(
                ElementFactory.createDiv(subClause.getText()));

        if (!subClause.getWarnings().isEmpty()) {
            Helping.addWarningDetails(infoLayout, subClause);
        }

        infoLayout.add(new Hr());

        return infoLayout;
    }

    public void initialize(Collection<ADocument.SubClause> subClauseCollection) {
        removeAll();
        VerticalLayout list = new VerticalLayout();
        for (var subclause : subClauseCollection) {
            list.add(subClauseComponentRenderer(subclause));
        }
        add(list);
    }

}
