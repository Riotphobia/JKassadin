package com.hawolt.ui;

import com.hawolt.data.Champion;
import com.hawolt.ui.callback.ISelectChampCallback;
import com.hawolt.ui.callback.ISelectSkinCallback;
import com.hawolt.ui.champion.JUnlockChampUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created: 27/07/2022 19:53
 * Author: Twitter @hawolt
 **/

public class JUnlockDetailUI extends JPanel {
    private final HashMap<String, JUnlockChampUI> components = new HashMap<>();
    private final JButton champBE, champRP, skin, champBE_skinRP, champRP_skinRP;

    private final CardLayout layout;
    private final JPanel main;

    private Champion champion;

    public JUnlockDetailUI(ISelectChampCallback champCallback, ISelectSkinCallback skinCallback, HashMap<String, Champion> map) {
        this.setLayout(new BorderLayout());
        JUnlockSelectUI selectUI = new JUnlockSelectUI(champCallback, map.values());
        this.add(selectUI, BorderLayout.NORTH);
        this.main = new JPanel();
        this.main.setLayout(layout = new CardLayout());
        for (Champion champion : map.values()) {
            JUnlockChampUI unlockChampUI = new JUnlockChampUI(skinCallback, champion);
            components.put(champion.getName(), unlockChampUI);
            this.main.add(champion.getName(), unlockChampUI);
        }
        this.main.add("blank", new JPanel());
        layout.show(main, "blank");
        this.add(main, BorderLayout.CENTER);
        JPanel interaction = new JPanel(new GridLayout(0, 1, 0, 5));
        interaction.setBorder(BorderFactory.createTitledBorder("Exploit"));
        this.skin = new JButton("Skin - Riot Points");
        this.skin.setEnabled(false);
        interaction.add(skin);
        this.champRP = new JButton("Champion - Riot Points");
        this.champRP.setEnabled(false);
        interaction.add(champRP);
        this.champBE = new JButton("Champion - Blue Essence");
        this.champBE.setEnabled(false);
        interaction.add(champBE);
        this.champRP_skinRP = new JButton("Champion + Skin - Riot Points");
        this.champRP_skinRP.setEnabled(false);
        interaction.add(champRP_skinRP);
        this.champBE_skinRP = new JButton("Champion + Skin - Blue Essence & Riot Points");
        this.champBE_skinRP.setEnabled(false);
        interaction.add(champBE_skinRP);
        this.add(interaction, BorderLayout.SOUTH);
    }

    public JButton getChampBESkinRP() {
        return champBE_skinRP;
    }

    public JButton getChampRPSkinRP() {
        return champRP_skinRP;
    }

    public JButton getChampBE() {
        return champBE;
    }

    public JButton getChampRP() {
        return champRP;
    }

    public JButton getSkin() {
        return skin;
    }

    public void toggle(Champion champion) {
        this.components.get(champion.getName()).getSkinUI().reset();
        this.layout.show(this.main, champion.getName());
        this.champion = champion;
    }

    public JUnlockChampUI getCurrent() {
        if (champion == null) return null;
        return components.get(champion.getName());
    }

    public Champion getChampion() {
        return champion;
    }
}
