package ua.kpi.analyzer.views;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import ua.kpi.analyzer.entities.ClassWithWarnings;

/**
 * @author Ihor Sytnik
 */
public class Helping {

    public static void addWarningDetails(HasComponents toAddTo, ClassWithWarnings classWithWarnings, String label) {
        if (classWithWarnings.getWarnings().size() != 0) {
            VerticalLayout clauseWarningLayout = new VerticalLayout();
            for (var warn : classWithWarnings.getWarnings()) {
                clauseWarningLayout.add(new Div(new Text(warn)));
            }
            toAddTo.add(new Div(clauseWarningLayout));
        }
    }
}
