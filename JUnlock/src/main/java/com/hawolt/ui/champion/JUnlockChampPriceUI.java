package com.hawolt.ui.champion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created: 27/07/2022 19:58
 * Author: Twitter @hawolt
 **/

public class JUnlockChampPriceUI extends JPanel {
    public JUnlockChampPriceUI(long be, long rp, long sale) {
        this.setLayout(new GridLayout(0, 2, 10, 0));
        this.add(new JLabel("Riot Points: " + (rp - sale), SwingConstants.LEFT));
        this.add(new JLabel("Blue Essence: " + be, SwingConstants.RIGHT));
        this.setBorder(new EmptyBorder(0, 5, 5, 5));
    }
}
