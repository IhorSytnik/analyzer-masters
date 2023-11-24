package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;
import java.util.Set;

/**
 * @author Ihor Sytnik
 */
@SpringComponent
public class SpecialtiesArea extends VerticalLayout {

    private Set<String> specialtiesToCheckFor;
    private final HorizontalLayout addArea = new HorizontalLayout();
    private final VerticalLayout addAreaWLabel = new VerticalLayout();
    private final TextField addTextField = new TextField();
    private final Button addButton;
    private Grid<String> specialtiesGrid = new Grid<>();
    private Properties locText;

    public SpecialtiesArea(
            @Autowired Set<String> specialtiesToCheckFor,
            @Autowired Properties locText) {

        this.specialtiesToCheckFor = specialtiesToCheckFor;
        this.locText = locText;

        addButton = new Button(locText.getProperty("ui.specialties.addButton"));
        addButton.addClickListener(event -> addSpecialty(addTextField.getValue()));
        addTextField.setPattern("\\d{3}");
        addArea.add(
                addTextField,
                addButton
        );
        addAreaWLabel.add(
                new NativeLabel(locText.getProperty("ui.specialties.addLabel")),
                addArea
        );

        specialtiesGrid.addComponentColumn((ValueProvider<String, Component>) this::createSpecialtyComp);
        specialtiesGrid.setDataProvider(new ListDataProvider<>(specialtiesToCheckFor));

        add(
                addAreaWLabel,
                specialtiesGrid
            );
    }

    private Component createSpecialtyComp(String specialtyNum) {
        HorizontalLayout cardLayout = new HorizontalLayout();
        Button removeButton = new Button(locText.getProperty("ui.specialties.removeButton"));
        removeButton.addClickListener(event -> removeSpecialty(specialtyNum));

        cardLayout.getElement().appendChild(
                ElementFactory.createStrong(specialtyNum),
                removeButton.getElement()
        );

        cardLayout.setAlignItems(Alignment.CENTER);
        cardLayout.setJustifyContentMode(JustifyContentMode.EVENLY);

        return cardLayout;
    }

    private void addSpecialty(String specialtyNum) {
        getUI().ifPresent(ui -> ui.access(() -> {
            if (addTextField.isInvalid()) {
                return;
            }
            specialtiesToCheckFor.add(specialtyNum);
            addTextField.clear();
            specialtiesGrid.getDataProvider().refreshAll();
        }));
    }

    private void removeSpecialty(String specialtyNum) {
        getUI().ifPresent(ui -> ui.access(() -> {
            specialtiesToCheckFor.remove(specialtyNum);
            specialtiesGrid.getDataProvider().refreshAll();
        }));
    }
}
