package com.hawolt.ui.champion;

import com.hawolt.data.Champion;
import com.hawolt.ui.callback.ISelectSkinCallback;
import com.hawolt.ui.champion.skin.JUnlockSkinUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created: 27/07/2022 19:43
 * Author: Twitter @hawolt
 **/

public class JUnlockChampUI extends JPanel {

    private final JUnlockSkinUI skinUI;

    public JUnlockChampUI(ISelectSkinCallback callback, Champion champion) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(new JUnlockChampPriceUI(champion.getBlueEssence(), champion.getRiotPoints(), champion.getSale()));
        this.skinUI = new JUnlockSkinUI(callback, champion.getList());
        this.add(skinUI);
    }

    public JUnlockSkinUI getSkinUI() {
        return skinUI;
    }
}
