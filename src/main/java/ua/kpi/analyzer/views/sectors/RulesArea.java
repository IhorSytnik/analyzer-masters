package ua.kpi.analyzer.views.sectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.analyzer.parsers.RulesImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ihor Sytnik
 */
@SpringComponent
public class RulesArea extends Div {

    private final HorizontalLayout addArea = new HorizontalLayout();

    private final VerticalLayout addClauseNumWLabel = new VerticalLayout();
    private final IntegerField addClauseNum = new IntegerField();

    private final VerticalLayout addMinWLabel = new VerticalLayout();
    private final HorizontalLayout addMinWCheck = new HorizontalLayout();
    private final IntegerField addMinNum = new IntegerField();
    private final Checkbox addMinCheck = new Checkbox();

    private final VerticalLayout addCitWLabel = new VerticalLayout();
    private final HorizontalLayout addCitWCheck = new HorizontalLayout();
    private final IntegerField addCitNum = new IntegerField();
    private final Checkbox addCitCheck = new Checkbox();

    private final VerticalLayout addReqWLabel = new VerticalLayout();
    private final Checkbox addReqCheck = new Checkbox();

    private final Button addButton;

    private Grid<Map.Entry<Integer, List<String>>> rulesGrid = new Grid<>();
    private Map<Integer, List<String>> clauseRules;
    private RulesImplementation rulesImplementation;
    private Properties locText;

    public RulesArea(
            @Autowired Map<Integer, List<String>> clauseRules,
            @Autowired RulesImplementation rulesImplementation,
            @Autowired Properties locText) {

        this.locText = locText;
        this.clauseRules = clauseRules;
        this.rulesImplementation = rulesImplementation;

        /*top*/
        addButton = new Button(locText.getProperty("ui.rules.add.button"));
        addButton.addClickListener(event -> addRule());

        addClauseNum.setStepButtonsVisible(true);
        addClauseNum.setValue(1);
        addClauseNum.setMin(1);
//        addClauseNum.setWidth(3, Unit.EM);
        addClauseNumWLabel.add(
                new NativeLabel(locText.getProperty("ui.rules.add.clauseNumber")),
                addClauseNum
        );

        addMinNum.setStepButtonsVisible(true);
        addMinNum.setValue(1);
        addMinNum.setMin(1);
//        addMinNum.setWidth(3, Unit.EM);
        addMinWCheck.add(
                addMinNum,
                addMinCheck
        );
        addMinWLabel.add(
                new NativeLabel(locText.getProperty("ui.rules.add.minimum")),
                addMinWCheck
        );

        addCitNum.setStepButtonsVisible(true);
        addCitNum.setValue(0);
        addCitNum.setMin(0);
//        addCitNum.setWidth(3, Unit.EM);
        addCitWCheck.add(
                addCitNum,
                addCitCheck
        );
        addCitWLabel.add(
                new NativeLabel(locText.getProperty("ui.rules.add.citations")),
                addCitWCheck
        );

        addReqWLabel.add(
                new NativeLabel(locText.getProperty("ui.rules.add.required")),
                addReqCheck
        );

        addArea.add(
                addClauseNumWLabel,
                addMinWLabel,
                addCitWLabel,
                addReqWLabel,
                addButton
        );

        /*middle*/
//        ListDataProvider<String> listDataProvider = DataProvider.ofCollection(specialtiesToCheckFor);

        rulesGrid.addColumn(Map.Entry::getKey).setHeader(locText.getProperty("ui.rules.grid.clauseNumberHeader"));
        rulesGrid.addColumn(this::createRules).setHeader(locText.getProperty("ui.rules.grid.rulesHeader"));

        rulesGrid.addComponentColumn(
                (ValueProvider<Map.Entry<Integer, List<String>>, Button>) this::createRemBtn);

        rulesGrid.setDataProvider(new ListDataProvider<>(clauseRules.entrySet()));

        /*bottom*/
        VerticalLayout tips = new VerticalLayout();
        tips.add(new Div(new Text(locText.getProperty("ui.rules.tips.minimum"))));
        tips.add(new Div(new Text(locText.getProperty("ui.rules.tips.citation"))));

        /*main*/
        VerticalLayout content = new VerticalLayout(
                addArea,
                rulesGrid,
                tips
        );
        Details details = new Details(locText.getProperty("ui.rules.label"), content);
        add(details);
    }

    private void addRule() {
        getUI().ifPresent(ui -> ui.access(() -> {
            List<String> ruleList = new ArrayList<>();
            if (addMinCheck.getValue()) {
                ruleList.add("min:" + addMinNum.getValue());
            }
            if (addCitCheck.getValue()) {
                ruleList.add(
                        addCitNum.getValue() != null && addCitNum.getValue() != 0 ?
                                ("cit:L" + addCitNum.getValue() + "y") :
                                "cit"
                );
            }
            if (addReqCheck.getValue()) {
                ruleList.add("req");
            }
            clauseRules.put(addClauseNum.getValue(), ruleList);

            try {
                rulesImplementation.updateRules();
            } catch (Exception e) {
                e.printStackTrace();
            }

            addClauseNum.setValue(1);

            addMinNum.setValue(1);
            addMinCheck.setValue(false);

            addCitNum.setValue(0);
            addCitCheck.setValue(false);

            addReqCheck.setValue(false);

            rulesGrid.getDataProvider().refreshAll();
        }));
    }

    private String createRules(Map.Entry<Integer, List<String>> entry) {
        StringBuilder stringBuilder = new StringBuilder();
        for (var r : entry.getValue()) {
            String[] split = r.split(":");
            RulesImplementation.Rule rule = RulesImplementation.Rule.valueOf(split[0]);
            switch (rule) {
                case min -> {
                    Matcher matcher = Pattern.compile("min:(\\d)").matcher(r);
                    matcher.find();
                    stringBuilder
                            .append(locText.getProperty("ui.rules.grid.rulesList.minimum").formatted(matcher.group(1)));
                }
                case cit -> {
                    stringBuilder.append(locText.getProperty("ui.rules.grid.rulesList.citation.regular"));
                    Matcher matcher = Pattern.compile("cit:L(\\d)y").matcher(r);
                    if (matcher.find()) {
                        stringBuilder.append(locText.getProperty("ui.rules.grid.rulesList.citation.years")
                                .formatted(matcher.group(1)));
                    }
                }
                case req -> stringBuilder.append(locText.getProperty("ui.rules.grid.rulesList.required"));
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    private Button createRemBtn(Map.Entry<Integer, List<String>> entry) {
        Button removeButton = new Button(locText.getProperty("ui.rules.grid.removeButton"));
        removeButton.addClickListener(event -> removeRule(entry.getKey()));
        return removeButton;
    }

    private void removeRule(Integer clause) {
        getUI().ifPresent(ui -> ui.access(() -> {
            clauseRules.remove(clause);
            try {
                rulesImplementation.updateRules();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rulesGrid.getDataProvider().refreshAll();
        }));
    }
}
