package ua.kpi.analyzer.views.elements;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
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


public class SubClauseVirtualListBasic extends Div {

    private final ComponentRenderer<Component, ADocument.SubClause> subClauseComponentRenderer =
        new ComponentRenderer<>(

            subClause -> {

                VerticalLayout infoLayout = new VerticalLayout();
//                infoLayout.setSpacing(false);
//                infoLayout.setPadding(false);
                infoLayout.getElement().appendChild(
                        ElementFactory.createDiv(subClause.getText()));

                /*Clause warnings*/
                if (!subClause.getWarnings().isEmpty()) {
                    Helping.addWarningDetails(infoLayout, subClause, "SubClause warnings");
                }

                infoLayout.add(new Hr());

                return infoLayout;
            });

    public void initialize(Collection<ADocument.SubClause> subClauseCollection) {
        removeAll();
        VirtualList<ADocument.SubClause> list = new VirtualList<>();
        list.setItems(subClauseCollection);
        list.setRenderer(subClauseComponentRenderer);
        add(list);
    }

}
