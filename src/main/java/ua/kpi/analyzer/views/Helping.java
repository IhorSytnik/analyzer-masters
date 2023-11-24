package ua.kpi.analyzer.views;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kpi.analyzer.entities.ContainsWarnings;

/**
 * @author Ihor Sytnik
 */
@Component
public class Helping {

    private static String warningBgColor;
    private static String warningFontColor;

    @Value("${warning.background.color}")
    private String bgc;
    @Value("${warning.font.color}")
    private String fc;

    @PostConstruct
    public void init() {
        warningBgColor = bgc;
        warningFontColor = fc;
    }

    public static void addWarningDetails(HasComponents toAddTo, ContainsWarnings classWithWarnings) {
        if (!classWithWarnings.getWarnings().isEmpty()) {
            VerticalLayout clauseWarningLayout = new VerticalLayout();
            for (var warn : classWithWarnings.getWarnings()) {
                clauseWarningLayout.add(new Div(new Text(warn)));
            }
            Div div = new Div(clauseWarningLayout);
            div.setWidth(100, Unit.PERCENTAGE);
            div.getStyle()
                    .set("background-color", warningBgColor)
                    .set("color", warningFontColor);
            toAddTo.add(div);
        }
    }
}
