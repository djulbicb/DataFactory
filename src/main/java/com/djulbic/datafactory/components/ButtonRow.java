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
        layout.setWidthFull();
        this.setWidthFull();
    }

    public void addComponents(Component... components){
        this.components.addAll(Arrays.asList(components));
        this.layout.addAndExpand(components);
    }
}
