package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ihor Sytnik
 */
@SpringComponent
public class SpecialtiesArea extends VerticalLayout {

    private final HorizontalLayout addArea = new HorizontalLayout();
    private final TextArea textArea = new TextArea();
    private final Button addButton = new Button("Add");
    @Autowired
    private Set<String> specialtiesToCheckFor;
    private Map<String, HorizontalLayout> specialtiesMap = new HashMap<>();

    public SpecialtiesArea(@Autowired Set<String> specialtiesToCheckFor) {

        this.specialtiesToCheckFor = specialtiesToCheckFor;

        addButton.addClickListener(event -> addSpecialty(textArea.getValue()));

        textArea.setPattern("\\d{3}");
        addArea.add(textArea, addButton);
        add(
                addArea
            );
        renderSpecialties();
    }

    private void renderSpecialties() {
        for (var spec : specialtiesToCheckFor) {
            createSpecialty(spec);
        }
    }

    private void createSpecialty(String specialtyNum) {
        if (specialtiesMap.containsKey(specialtyNum)) {
            return;
        }

        HorizontalLayout cardLayout = new HorizontalLayout();
        Span span = new Span(specialtyNum);
        Button removeButton = new Button("Remove");
        removeButton.addClickListener(event -> removeSpecialty(specialtyNum));

        cardLayout.add(span, removeButton);

        specialtiesMap.put(specialtyNum, cardLayout);
        specialtiesToCheckFor.add(specialtyNum);

        add(cardLayout);
    }

    private void addSpecialty(String specialtyNum) {
        getUI().ifPresent(ui -> ui.access(() -> {

            createSpecialty(specialtyNum);

            textArea.clear();
        }));
    }

    private void removeSpecialty(String specialtyNum) {
        getUI().ifPresent(ui -> ui.access(() -> {
            specialtiesMap.remove(specialtyNum).removeFromParent();
            specialtiesToCheckFor.remove(specialtyNum);
        }));
    }
}
