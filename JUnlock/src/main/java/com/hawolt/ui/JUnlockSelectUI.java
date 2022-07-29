package com.hawolt.ui;

import com.hawolt.data.Champion;
import com.hawolt.ui.callback.ISelectChampCallback;
import com.hawolt.ui.callback.ISelectSkinCallback;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Created: 27/07/2022 19:45
 * Author: Twitter @hawolt
 **/

public class JUnlockSelectUI extends JPanel {

    public JUnlockSelectUI(ISelectChampCallback callback, Collection<Champion> collection) {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Select Champion"));
        try {
            SortedComboBoxModel<Champion> model = new SortedComboBoxModel<>(collection.toArray(new Champion[0]));
            JComboBox<Champion> championJComboBox = new JComboBox<>(model);
            championJComboBox.setSelectedIndex(-1);
            championJComboBox.addActionListener(listener -> callback.onSelection((Champion) championJComboBox.getSelectedItem()));
            this.add(championJComboBox, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
