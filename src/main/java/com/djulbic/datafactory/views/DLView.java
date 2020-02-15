package com.djulbic.datafactory.views;

import com.djulbic.datafactory.DataLibraryMethodCallParser;
import com.djulbic.datafactory.MethodCallParser;
import com.djulbic.datafactory.components.ButtonRow;
import com.djulbic.datafactory.metadata.providers.MySQLMetadataProvider;
import com.djulbic.datafactory.model.ColumnSql;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;
import data.DataLibrary;
import data.DataLibraryMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Route("")
// @PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
@Theme(value = Lumo.class, variant = Material.DARK)
@CssImport("styles/custom-styles.css")
@HtmlImport("html/html.html")
public class DLView extends HorizontalLayout {

    String connectionUrl = "jdbc:mysql://localhost:3306";
    String username = "root";
    String password = "";
    MySQLMetadataProvider metadataProvider = new MySQLMetadataProvider(connectionUrl, username, password);

    @Autowired
    DataLibrary dataLibrary;
    DataLibraryMetadata metadata = new DataLibraryMetadata();

    ComboBox<String> comboBoxDatabase = new ComboBox<>();
    ComboBox<String> comboBoxTables = new ComboBox<>();
    VerticalLayout rowLayout = new VerticalLayout();

    public DLView() {
        this.setHeightFull();

        HorizontalLayout parent = new HorizontalLayout();
        parent.setClassName("container");
        parent.setHeightFull();
        parent.setSpacing(false);

        VerticalLayout historyLogLayout = buildHistoryLogLayout();
        historyLogLayout.setSpacing(false);
        TextField url = new TextField();
        TextField username = new TextField();
        TextField password = new TextField();

        historyLogLayout.add(url, username, password);

        VerticalLayout controlLayout = new VerticalLayout();


        parent.add(historyLogLayout);
        parent.add(controlLayout);


        controlLayout.add(buildRowButtonLayout());
        comboBoxDatabase.addValueChangeListener(event -> {
            List<String> tables = metadataProvider.getTables(event.getValue());
            comboBoxTables.setItems(tables);
        });

        comboBoxDatabase.setItems(metadataProvider.getDatabases());

        controlLayout.add(comboBoxDatabase);
        controlLayout.add(comboBoxTables);
        controlLayout.add(rowLayout);
        controlLayout.setSpacing(false);
        controlLayout.setClassName("controlLayout");

        comboBoxTables.addValueChangeListener(event -> {
            List<ColumnSql> columns = metadataProvider.getColumns(comboBoxDatabase.getValue(), comboBoxTables.getValue());
            rowLayout.removeAll();

            for (ColumnSql column : columns) {
                SqlEntryRow sqlEntryRow = new SqlEntryRow(column.getName(), column.getType(), column.getSize());
                rowLayout.add(sqlEntryRow);
            }

        });

        addComponentAsFirst(parent);
        setWidthFull();
        //layout.setSpacing(false);
        rowLayout.setWidthFull();
        rowLayout.setSpacing(false);
        rowLayout.setClassName("rowLayout");

    }

    private VerticalLayout buildHistoryLogLayout() {
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout rowButton = new HorizontalLayout();

        rowButton.add(new Button("Create"));
        rowButton.add(new Button("Update"));
        rowButton.add(new Button("Delete"));
        rowButton.add(new Button("Import"));
        rowButton.add(new Button("Export"));
        layout.add(rowButton);

        VerticalLayout logs = new VerticalLayout();

        layout.add(logs);

        logs.addClassName("border");
        layout.setMaxWidth("30%");

        return layout;
    }

    HorizontalLayout buildRowButtonLayout() {
        HorizontalLayout rowLayout = new HorizontalLayout();

        rowLayout.add(buildInsertButton("Insert", 1));

        Label label = new Label("Insert multiple");
        //label.setWidthFull();
        rowLayout.add(label);

        ButtonRow row = new ButtonRow();
        row.addComponents(buildInsertButton(25), buildInsertButton(50), buildInsertButton(100), buildInsertButton(100), buildInsertButton(500), buildInsertButton(1000));

        rowLayout.add(row);

        rowLayout.add(new TextField());
        rowLayout.add(new Button("Custom"));

        return rowLayout;
    }

    private Button buildInsertButton(int numberOfTimes) {
        return buildInsertButton(numberOfTimes + "", numberOfTimes);
    }

    private Button buildInsertButton(String title, int numberOfTimes) {
        Button button = new Button();
        button.setText(title);

        button.addClickListener(event -> {
            try {
                List<String> insertQueries = new ArrayList<>();
                for (int i = 0; i < numberOfTimes; i++) {
                    insertQueries.add(getInsertQuery());
                }
                boolean isInserted = metadataProvider.insertQuery(insertQueries);
                if (isInserted) {
                    Notification notification = new Notification(insertQueries.size() + " entries have been succesfully entered", 3000);
                    notification.open();
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    public String getInsertQuery() throws InvocationTargetException, IllegalAccessException {
        String database = "`" + comboBoxDatabase.getValue() + "`";
        String table = "`" + comboBoxTables.getValue() + "`";
        String databaseWithTable = database + "." + table;

        List<SqlEntryRow> sqlEntryRows = new ArrayList<>();

        List<Component> collect = rowLayout.getChildren().collect(Collectors.toList());
        for (Component component : collect) {
            if (component instanceof SqlEntryRow) {
                sqlEntryRows.add((SqlEntryRow) component);
            }
        }


        DataLibrary dl = DataLibrary.getEnglishData();
        DataLibraryMethodCallParser methodCallParser = new DataLibraryMethodCallParser();
        String tables = "";
        String values = "";
        for (SqlEntryRow row : sqlEntryRows) {
            if (row.getCheckbox().isChecked()){
                continue;
            }

            tables += (row.getTxtColumnName().getValue() + ",");
            String methodName = row.getComboBox().getValue().getName();
            String params = row.getInput().getValue();
            if (!params.isEmpty()) {
                params = "(" + params + ")";
            } else {
                params = "()";
            }


            String methodCall = methodName + params;

            Object parse = methodCallParser.parse(dl, methodCall);
            String val = parse.toString();

            if(row.getTxtColumnType().getValue().equalsIgnoreCase("VARCHAR")){
                val = "'" + val + "'" ;
            }

            values += val + ",";
            System.out.println(parse);
        }

        tables = tables.substring(0, tables.length() - 1);
        values = values.substring(0, values.length() - 1);
        String syntax = String.format("INSERT INTO %s (%s) VALUES (%s)", databaseWithTable, tables, values);
        System.out.println(syntax);

        return syntax;

    }

}



