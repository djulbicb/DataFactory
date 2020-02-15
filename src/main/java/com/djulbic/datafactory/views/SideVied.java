package com.djulbic.datafactory.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.material.Material;

@Route("side")
// @PWA(name = "Project Base for Vaadin Flow", shortName = "Project Base")
@Theme(value = Lumo.class, variant = Material.DARK)
@CssImport("styles/custom-styles.css")
@HtmlImport("html/html.html")
public class SideVied extends HorizontalLayout {

    public SideVied() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addAndExpand(new Button("ssss"));
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.addAndExpand(new Button("ssss"));
        add(verticalLayout);
        add(verticalLayout1);
    }
}
