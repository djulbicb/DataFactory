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

    @Autowired
    DataLibrary dataLibrary;
    DataLibraryMetadata metadata = new DataLibraryMetadata();

    ComboBox<String> comboBoxDatabase = new ComboBox<>();
    ComboBox<String> comboBoxTables = new ComboBox<>();
    VerticalLayout rowLayout = new VerticalLayout();

    public DLView() {
        Button button = new Button("Tesxt");
        button.addClickListener(event -> {
           insert();
        });

        VerticalLayout layout = new VerticalLayout();




        MySQLMetadataProvider metadataProvider = new MySQLMetadataProvider(connectionUrl, username, password);

        comboBoxDatabase.addValueChangeListener(event -> {
            List<String> tables = metadataProvider.getTables(event.getValue());
            comboBoxTables.setItems(tables);
        });

        comboBoxDatabase.setItems(metadataProvider.getDatabases());
        layout.add(button);
        button.addClickListener(event -> {
            System.out.println("heee"); insert();
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

    public List<SqlEntryRow> insert(){
        List<SqlEntryRow> sqlEntryRows = new ArrayList<>();

        List<Component> collect = rowLayout.getChildren().collect(Collectors.toList());
        for (Component component : collect) {
            if (component instanceof SqlEntryRow){
                sqlEntryRows.add((SqlEntryRow) component);
            }
        }


        // INSERT INTO `table_name`(column_1,column_2,...) VALUES (value_1,value_2,...);
        String tables = "";
        String values = "";
        for (SqlEntryRow row : sqlEntryRows) {
            tables += row.getTxtColumnName().getValue() + ",";
        }

        for (SqlEntryRow row : sqlEntryRows) {
            values += row.getComboBox().getValue() + ",";
        }

        String syntax = String.format("INSERT INTO %s (%s) VALUES (%s)", "database", tables, values);
        System.out.println(syntax);

        return sqlEntryRows;
    }



}
