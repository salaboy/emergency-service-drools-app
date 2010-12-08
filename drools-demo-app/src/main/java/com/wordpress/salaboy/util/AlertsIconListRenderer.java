package com.wordpress.salaboy.util;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class AlertsIconListRenderer
	extends DefaultListCellRenderer {

	private List<Icon> icons = null;

	public AlertsIconListRenderer() {
            icons = new ArrayList<Icon>();
            icons.add(new ImageIcon(getClass().getResource("/data/png/attack.png")));
            icons.add(new ImageIcon(getClass().getResource("/data/png/nopulse.png")));
	}

	@Override
	public Component getListCellRendererComponent(
		JList list, Object value, int index,
		boolean isSelected, boolean cellHasFocus) {

		// Get the renderer component from parent class
		JLabel label =
			(JLabel) super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);

		// Get icon to use for the list item value
                if (value instanceof String){
                    String stringValue = value.toString();
                    Icon icon = null;
                    Color color = Color.black;
                    if (stringValue.toLowerCase().contains("vital signs")){
                        icon = icons.get(0);
                        color = Color.orange;
                    }else if (stringValue.toLowerCase().contains("heart attack")){
                        icon = icons.get(1);
                        color = Color.red;
                    }
                    label.setForeground(color);
                    // Set icon to display for value
                    if (icon != null){
                        label.setIcon(icon);
                    }
                }

		return label;
	}

}