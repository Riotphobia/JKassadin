package com.hawolt.ui;

import com.hawolt.JUnlock;
import com.hawolt.data.Wallet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created: 27/07/2022 20:50
 * Author: Twitter @hawolt
 **/

public class JUnlockWalletUI extends JPanel {
    private final JLabel be, rp;

    public JUnlockWalletUI(Wallet wallet) {
        this.setLayout(new GridLayout(0, 2, 10, 0));
        this.add(this.rp = new JLabel("Riot Points: " + wallet.getRiotPoints(), SwingConstants.LEFT));
        this.add(this.be = new JLabel("Blue Essence: " + wallet.getBlueEssence(), SwingConstants.RIGHT));
        this.setBorder(BorderFactory.createTitledBorder("Wallet"));
    }

    public void update(Wallet wallet) {
        this.be.setText("Blue Essence: " + wallet.getBlueEssence());
        this.rp.setText("Riot Points: " + wallet.getRiotPoints());
    }
}
