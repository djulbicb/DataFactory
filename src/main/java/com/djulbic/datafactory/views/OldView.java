package com.djulbic.datafactory.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;

@Route("old")
// @PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
@Theme(value = Lumo.class, variant = Material.DARK)
@CssImport("styles/custom-styles.css")
@HtmlImport("html/html.html")
public class OldView extends VerticalLayout {

    public OldView() {
        setHeightFull();
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeightFull();

        HorizontalLayout rowLayout = new HorizontalLayout();
        addComponentAsFirst(mainLayout);

        VerticalLayout topLayout = new VerticalLayout();
        VerticalLayout rightLayout = new VerticalLayout();
        VerticalLayout centerLayout = new VerticalLayout();
        VerticalLayout leftLayout = new VerticalLayout();
        VerticalLayout bottomLayout = new VerticalLayout();

        mainLayout.addAndExpand(topLayout, rowLayout, bottomLayout);
        rowLayout.addAndExpand(rightLayout, centerLayout, leftLayout);
    }
}
