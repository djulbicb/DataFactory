package com.djulbic.datafactory.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ClickableRenderer;

import java.awt.*;

public class UncheckBox extends Button  {

    private boolean checked = false;
    private Icon closeIcon  = new Icon(VaadinIcon.CLOSE);

    public UncheckBox() {


        closeIcon.setSize("30px");
        setIcon(closeIcon);
        setClassName("uncheckbox");

      closeIcon.setVisible(false);

        this.addClickListener(event -> {
            System.out.println("CLicked");
            checked =!checked;
            closeIcon.setVisible(checked);
        });
    }

    public boolean isChecked(){
        return checked;
    }




}
