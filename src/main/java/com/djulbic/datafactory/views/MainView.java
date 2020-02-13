package com.djulbic.datafactory.views;

import com.djulbic.datafactory.components.ButtonRow;
import com.djulbic.datafactory.metadata.providers.MySQLMetadataProvider;
import com.djulbic.datafactory.model.ColumnSql;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;
import data.DataLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Route("")
//// @PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
//@Theme(value = Lumo.class, variant = Material.DARK)
//@CssImport("styles/custom-styles.css")
//@HtmlImport("html/html.html")
public class MainView extends VerticalLayout {

    @Autowired
    DataLibrary dataLibrary;
    String connectionUrl = "jdbc:mysql://localhost:3306";
    String username = "root";
    String password = "";

    public MainView() throws SQLException {
        MySQLMetadataProvider metadataProvider = new MySQLMetadataProvider(connectionUrl, username, password);

        VerticalLayout layout = new VerticalLayout();

        ComboBox<String> comboBoxDatabase = new ComboBox<>();
        ComboBox<String> comboBoxTables = new ComboBox<>();
        comboBoxDatabase.addValueChangeListener(event -> {
            List<String> tables = metadataProvider.getTables(event.getValue());
            comboBoxTables.setItems(tables);
        });

        comboBoxDatabase.setItems(metadataProvider.getDatabases());
        layout.add(comboBoxDatabase);
        layout.add(comboBoxTables);


        layout.add(menu());
        Button button = new Button("Button");
        button.addClickListener(event -> {
            Notification notification = new Notification(
                    "This notification has text content", 3000);
            System.out.println("Clicked");
            notification.open();
        });
        button.addThemeName("primary");
        addComponentAsFirst(layout);

        layout.add(button);

        Connection connection = getDataSource().getConnection();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS bojan.bojan (id INT);");



        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTypeInfo();
        List<String> list = new ArrayList<String>();
        while (resultSet.next()) {
            String typeName = resultSet.getString("TYPE_NAME").toUpperCase();
            list.add(typeName);
        }
        System.out.println(list); // BIT, BOOL, TINYINT, TINYINT UNSIGNED, BIGINT

        // https://vaadin.com/docs/flow/components/tutorial-flow-grid.html
        List<ColumnSql> columnsMetadata = getColumnsMetadata(metaData);
        Grid<ColumnSql> grid = new Grid<>(ColumnSql.class);
        grid.setItems(columnsMetadata);
        grid.addThemeName("my-input-theme");
        grid.addClassName("mystyle");
        grid.getColumnByKey("name").setHeader("Column name");
        grid.getColumnByKey("type").setHeader("Column type");
        grid.getColumnByKey("size").setHeader("Column size");
        grid.addComponentColumn(columnSql -> {
           return new TextField();
        });

grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
grid.addThemeName("test");
        grid.addComponentColumn(columnSql -> {
            ComboBox comboBox = new ComboBox();
            List<String> strings = new ArrayList<>();
            Method[] declaredMethods = dataLibrary.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                Parameter[] parameters = method.getParameters();
                System.out.println(parameters.length);
                if (parameters.length > 0){
                    for (Parameter parameter : parameters) {
                        System.out.println(parameter.getName());
                    }

                }
                strings.add(method.getName());
                System.out.println("----");
            }
            comboBox.setItems(strings);
            return comboBox;
        });



        grid.addItemClickListener(event -> {
            System.out.println(event);
            System.out.println(event.getColumn().getChildren());
            System.out.println(event.getItem());
        });

        layout.add(grid);

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

        layout.addAndExpand(rowLayout);
        resultSet.close();
        connection.close();




    }

    public static List<Method> getVirtualMethods(Class scanClass){
        Method[] methods = scanClass.getDeclaredMethods();
        List<Method> virtualMethods = new ArrayList<>();

        for (Method method : methods) {
            if (!method.toGenericString().contains("static")){
                virtualMethods.add(method);
            }
        }

        return virtualMethods;
    }

    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(com.mysql.cj.jdbc.Driver.class.getName()); //"com.mysql.jdbc.Driver"
        dataSourceBuilder.url("jdbc:mysql://localhost:3306");//bojan?createDatabaseIfNotExist=true");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

    public static List<ColumnSql> getColumnsMetadata(DatabaseMetaData metaData) throws SQLException {
        ResultSet resultSet = metaData.getColumns(null, null, "bojan", null); // ovo je db
        List<ColumnSql> columnSql = new ArrayList<>();
        while (resultSet.next()) {
            String name = resultSet.getString("COLUMN_NAME");
            String type = resultSet.getString("TYPE_NAME");
            int size = resultSet.getInt("COLUMN_SIZE");

            ColumnSql column = new ColumnSql(name, type, size);
            // System.out.println("Column name: [" + name + "];" + "type: [" + type + "]; size: [" + size + "]"); // Column name: [id]; type: [INT]; size: [10]

            columnSql.add(column);
        }
        return columnSql;
    }

    public MenuBar menu(){
        MenuBar menuBar = new MenuBar();
        menuBar.setOpenOnHover(true);
        Text selected = new Text("");
        Div message = new Div(new Text("Selected: "), selected);

        MenuItem project = menuBar.addItem("Project");
        MenuItem account = menuBar.addItem("Account");
        menuBar.addItem("API Help", e -> selected.setText("Sign Out"));

        SubMenu projectSubMenu = project.getSubMenu();
        MenuItem users = projectSubMenu.addItem("Users");
        MenuItem billing = projectSubMenu.addItem("Billing");

        SubMenu usersSubMenu = users.getSubMenu();
        usersSubMenu.addItem("List", e -> selected.setText("List"));
        usersSubMenu.addItem("Add", e -> selected.setText("Add"));

        SubMenu billingSubMenu = billing.getSubMenu();
        billingSubMenu.addItem("Invoices", e -> selected.setText("Invoices"));
        billingSubMenu.addItem("Balance Events",
                e -> selected.setText("Balance Events"));

        account.getSubMenu().addItem("Edit Profile",
                e -> selected.setText("Edit Profile"));
        account.getSubMenu().addItem("Privacy Settings",
                e -> selected.setText("Privacy Settings"));

        return menuBar;
    }
}


// TEST material-color-light find where it is