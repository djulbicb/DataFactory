package com.djulbic.datafactory.views;

import com.djulbic.datafactory.components.ButtonRow;
import com.djulbic.datafactory.metadata.providers.MySQLMetadataProvider;
import com.djulbic.datafactory.model.ColumnSql;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Route("")
// @PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
@Theme(value = Lumo.class, variant = Material.DARK)
@CssImport("styles/custom-styles.css")
@HtmlImport("html/html.html")
public class DLView extends VerticalLayout {

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

        this.setMaxWidth("60%");


        Button button = new Button("Tesxt");
        button.addClickListener(event -> {
            try {
               String insertQuery = getInsertQuery();
                boolean isInserted = metadataProvider.insertQuery(insertQuery);
                if (isInserted){
                    Notification notification = new Notification("This notification has text content", 3000);
                    notification.open();
                }


            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        VerticalLayout layout = new VerticalLayout();






        comboBoxDatabase.addValueChangeListener(event -> {
            List<String> tables = metadataProvider.getTables(event.getValue());
            comboBoxTables.setItems(tables);
        });

        comboBoxDatabase.setItems(metadataProvider.getDatabases());
        layout.add(button);
        button.addClickListener(event -> {
            System.out.println("heee");
            try {
                getInsertQuery();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        layout.add(comboBoxDatabase);
        layout.add(comboBoxTables);
        layout.add(rowLayout);


        comboBoxTables.addValueChangeListener(event -> {
            List<ColumnSql> columns = metadataProvider.getColumns(comboBoxDatabase.getValue(), comboBoxTables.getValue());
            rowLayout.removeAll();

            for (ColumnSql column : columns) {
                SqlEntryRow sqlEntryRow = new SqlEntryRow(column.getName(), column.getType());
                rowLayout.add(sqlEntryRow);
            }

        });

        addComponentAsFirst(layout);
        layout.setWidthFull();
        layout.setSpacing(false);
        rowLayout.setWidthFull();
        rowLayout.setSpacing(false);

        layout.addAndExpand(buildRowButtonLayout());
    }



    HorizontalLayout buildRowButtonLayout(){
        HorizontalLayout rowLayout = new HorizontalLayout();

        rowLayout.add(new Button("Insert"));

        Label label = new Label("Insert multiple");
        //label.setWidthFull();
        rowLayout.add(label);

        ButtonRow row = new ButtonRow();
        row.addComponents(new Button("25"), new Button("50"), new Button("100"),new Button("250") ,new Button("500") ,new Button("1000"));

        rowLayout.add(row);

        rowLayout.add(new TextField());
        rowLayout.add(new Button("Custom"));

        return rowLayout;
    }

    public String getInsertQuery() throws InvocationTargetException, IllegalAccessException {
        String database = "`" + comboBoxDatabase.getValue() + "`";
        String table = "`" + comboBoxTables.getValue() + "`";
        String databaseWithTable = database + "." + table;

        List<SqlEntryRow> sqlEntryRows = new ArrayList<>();

        List<Component> collect = rowLayout.getChildren().collect(Collectors.toList());
        for (Component component : collect) {
            if (component instanceof SqlEntryRow){
                sqlEntryRows.add((SqlEntryRow) component);
            }
        }


        // INSERT INTO `table_name`(column_1,column_2,...) VALUES (value_1,value_2,...);
        // INSERT INTO `test`.`bojan` (name,last,number,decim) VALUES ('Tommy','Moncayo',14,7758.22138)
        String tables = "";
        String values = "";
        for (SqlEntryRow row : sqlEntryRows) {
            if (row.getCheckbox().isChecked()){
                continue;
            }
            tables += row.getTxtColumnName().getValue() + ",";
        }
        tables = tables.substring(0, tables.length() - 1);

        for (SqlEntryRow row : sqlEntryRows) {
            if (row.getCheckbox().isChecked()){
                continue;
            }
            Method method = row.getComboBox().getValue();
            Object ret = method.invoke(dataLibrary);
            String value = ret.toString();

            if (row.getTxtColumnType().getValue().equals("VARCHAR")){
                value = "'" + value + "'";
            }

            values += value + ",";
        }
        values = values.substring(0, values.length() - 1);

        String syntax = String.format("INSERT INTO %s (%s) VALUES (%s)", databaseWithTable, tables, values);
        System.out.println(syntax);

        return syntax;
    }



}
