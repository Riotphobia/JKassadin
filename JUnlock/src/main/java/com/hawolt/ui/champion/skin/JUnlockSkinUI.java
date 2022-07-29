package com.hawolt.ui.champion.skin;

import com.hawolt.data.Skin;
import com.hawolt.ui.SortedComboBoxModel;
import com.hawolt.ui.callback.ISelectSkinCallback;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created: 27/07/2022 19:43
 * Author: Twitter @hawolt
 **/

public class JUnlockSkinUI extends JPanel {

    private final JComboBox<Skin> skinJComboBox;

    public JUnlockSkinUI(ISelectSkinCallback callback, List<Skin> list) {
        this.setLayout(new BorderLayout());
        SortedComboBoxModel<Skin> model = new SortedComboBoxModel<>(list.toArray(new Skin[0]));
        this.skinJComboBox = new JComboBox<>(model);
        this.skinJComboBox.setSelectedIndex(-1);
        this.add(skinJComboBox, BorderLayout.NORTH);
        this.setBorder(BorderFactory.createTitledBorder("Skin Information"));
        CardLayout layout = new CardLayout();
        JPanel main = new JPanel(layout);
        for (Skin skin : list) {
            main.add(skin.getName(), new JUnlockSkinPriceUI(skin.getCost(), skin.getSale()));
        }
        main.add("blank", new JLabel());
        layout.show(main, "blank");
        this.add(main, BorderLayout.SOUTH);
        this.skinJComboBox.addActionListener(listener -> {
            Skin skin = this.skinJComboBox.getItemAt(this.skinJComboBox.getSelectedIndex());
            if (skin == null) layout.show(main, "blank");
            else {
                layout.show(main, skin.getName());
                callback.onSelection(skin);
            }
        });
    }

    public Skin getSelectedSkin() {
        return this.skinJComboBox.getItemAt(this.skinJComboBox.getSelectedIndex());
    }

    public void reset() {
        this.skinJComboBox.setSelectedIndex(-1);
    }
}
