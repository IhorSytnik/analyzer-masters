package ua.kpi.analyzer.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.analyzer.views.sectors.RulesArea;
import ua.kpi.analyzer.views.sectors.ScanResults;
import ua.kpi.analyzer.views.sectors.UploadAndSpecialties;

@Route("")
@PreserveOnRefresh
@VaadinSessionScope
public class MainView extends HorizontalLayout {

    public MainView(
            @Autowired UploadAndSpecialties uploadAndSpecialties,
            @Autowired ScanResults scanResults,
            @Autowired RulesArea rulesArea
    ) {
        setMinWidth(100, Unit.PERCENTAGE);
//        setMinHeight(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);

        uploadAndSpecialties.setWidth(25, Unit.PERCENTAGE);
//        uploadAndSpecialties.setHeight(100, Unit.PERCENTAGE);
        uploadAndSpecialties.getStyle().set("background-color", "#fcfcfc");

        scanResults.setHeight(100, Unit.PERCENTAGE);
        scanResults.getStyle().set("overflow", "auto");

        rulesArea.getStyle()
                .set("position", "fixed")
                .set("top", "5px")
                .set("right", "5px")
                .set("background-color", "#fcfcfc");;

        add(
                uploadAndSpecialties,
                scanResults,
                rulesArea);
    }
}