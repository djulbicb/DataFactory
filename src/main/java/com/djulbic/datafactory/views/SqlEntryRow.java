package com.djulbic.datafactory.views;

import com.djulbic.datafactory.MapMySQLTypesToDataLibrary;
import com.djulbic.datafactory.components.UncheckBox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.lang.reflect.Method;
import java.util.List;

public class SqlEntryRow extends HorizontalLayout{

    private TextField txtColumnName;
    private TextField txtColumnType;
    private UncheckBox checkbox;
    private ComboBox<Method> comboBox;
    private TextField input;
    private TextField delimiter;

    public TextField getTxtColumnName() {
        return txtColumnName;
    }

    public void setTxtColumnName(TextField txtColumnName) {
        this.txtColumnName = txtColumnName;
    }

    public TextField getTxtColumnType() {
        return txtColumnType;
    }

    public void setTxtColumnType(TextField txtColumnType) {
        this.txtColumnType = txtColumnType;
    }

    public UncheckBox getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(UncheckBox checkbox) {
        this.checkbox = checkbox;
    }

    public ComboBox<Method> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<Method> comboBox) {
        this.comboBox = comboBox;
    }

    public TextField getInput() {
        return input;
    }

    public void setInput(TextField input) {
        this.input = input;
    }

    public TextField getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(TextField delimiter) {
        this.delimiter = delimiter;
    }

    public SqlEntryRow(String columnName, String columnType) {

        checkbox = new UncheckBox();

        txtColumnName = new TextField();
        txtColumnName.setValue(columnName);
        txtColumnName.setReadOnly(true);
        txtColumnName.setPlaceholder("Column name");

        txtColumnType = new TextField();
        txtColumnType.setReadOnly(true);
        txtColumnType.setValue(columnType);
        txtColumnType.setPlaceholder("Column type");

       comboBox = new ComboBox<>();
        comboBox.setPlaceholder("method");

        input = new TextField();
        input.setPlaceholder("Input");
        input.setVisible(false);

        delimiter = new TextField();
        delimiter.setPlaceholder("delimiter");
        delimiter.setVisible(false);

        add(checkbox, txtColumnName, txtColumnType, comboBox, input, delimiter);
        setFlexGrow(1, txtColumnName);

        MapMySQLTypesToDataLibrary map = new MapMySQLTypesToDataLibrary();
        List<Method> varchar = map.getMethods(columnType);

        comboBox.setItems(varchar);
        comboBox.setItemLabelGenerator(item -> item.getName());
        comboBox.addValueChangeListener(event -> {
            Method value = event.getValue();
            if (value.getParameterCount() == 0) {
                input.setVisible(false);
            } else {
                input.setVisible(true);
            }

            if (value.getParameterCount() > 0 && value.isVarArgs()) {
                delimiter.setVisible(true);
            } else {
                delimiter.setVisible(false);
            }
        });



    }
}
