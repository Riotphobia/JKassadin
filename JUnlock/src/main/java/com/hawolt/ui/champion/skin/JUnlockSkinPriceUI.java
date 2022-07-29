package com.hawolt.ui.champion.skin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created: 27/07/2022 19:58
 * Author: Twitter @hawolt
 **/

public class JUnlockSkinPriceUI extends JPanel {
    public JUnlockSkinPriceUI(long rp, long sale) {
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Riot Points: " + (rp - sale), SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
