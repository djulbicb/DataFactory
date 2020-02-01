package com.djulbic.datafactory.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ButtonRow extends HorizontalLayout {
    List<Component> components = new ArrayList<>();
    HorizontalLayout layout = new HorizontalLayout();
    public ButtonRow() {
        this.addComponentAsFirst(layout);
        layout.addClassName("vaadin-button-row");

    }

    public ButtonRow(Component... components) {
        this.components.addAll(Arrays.asList(components));
        this.addComponents(components);

    }

    public void addComponent(Component component){
        this.components.add(component);
        layout.add(component);
    }

    public void addComponents(Component... components){
        this.components.addAll(Arrays.asList(components));
        this.layout.add(components);
    }
}
