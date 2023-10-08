package ua.kpi.analyzer.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.analyzer.views.sectors.ScanResults;
import ua.kpi.analyzer.views.sectors.UploadAndSpecialties;

@Route("")
@PreserveOnRefresh
@VaadinSessionScope
public class MainView extends HorizontalLayout {

    public MainView(
            @Autowired UploadAndSpecialties uploadAndSpecialties,
            @Autowired ScanResults scanResults
    ) {
        setSizeFull();
        getStyle().set("display", "flex");

        uploadAndSpecialties.setWidth(25, Unit.PERCENTAGE);
        uploadAndSpecialties.setHeight(100, Unit.PERCENTAGE);
        uploadAndSpecialties.getStyle().set("background-color", "#fcfcfc");

        add(uploadAndSpecialties, scanResults);
    }
}