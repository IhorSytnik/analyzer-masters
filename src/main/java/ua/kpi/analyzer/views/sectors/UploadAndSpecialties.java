package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class UploadAndSpecialties extends VerticalLayout {

    public UploadAndSpecialties(
            @Autowired UploadArea uploadArea,
            @Autowired SpecialtiesArea specialtiesArea
    ) {
        uploadArea.getUploadField().addSucceededListener(e -> uploadArea.hideErrorField());

        add(
                uploadArea,
                new Hr(),
                specialtiesArea
        );
    }

}
