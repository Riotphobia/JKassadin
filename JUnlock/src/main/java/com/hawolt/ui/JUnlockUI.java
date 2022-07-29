package com.hawolt.ui;

import com.hawolt.JUnlock;
import com.hawolt.data.Champion;
import com.hawolt.data.Session;
import com.hawolt.data.Skin;
import com.hawolt.data.Wallet;
import com.hawolt.ui.callback.ISelectChampCallback;
import com.hawolt.ui.callback.ISelectSkinCallback;
import com.hawolt.ui.callback.IValueCallback;
import com.hawolt.ui.callback.WalletCallback;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created: 27/07/2022 19:39
 * Author: Twitter @hawolt
 **/

public class JUnlockUI extends JFrame implements ISelectChampCallback, ISelectSkinCallback {
    private final JUnlockDetailUI detailUI;
    private final JUnlockWalletUI walletUI;
    private final WalletCallback callback;

    private Champion lastSelectedChampion;
    private Skin lastSelectedSkin;

    public JUnlockUI(JUnlock unlock, String title, HashMap<String, Champion> map) {
        this.setTitle(title);
        this.callback = unlock;
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        this.walletUI = new JUnlockWalletUI(callback.getWallet());
        this.add(walletUI, BorderLayout.NORTH);
        this.detailUI = new JUnlockDetailUI(this, this, map);
        container.add(detailUI, BorderLayout.CENTER);
        JUnlockRefundUI refundUI = new JUnlockRefundUI(unlock);
        container.add(refundUI, BorderLayout.SOUTH);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public JUnlockWalletUI getWalletUI() {
        return walletUI;
    }

    public JUnlockDetailUI getDetailUI() {
        return detailUI;
    }


    public JButton getSkin() {
        return this.detailUI.getSkin();
    }

    public JButton getChampBE() {
        return this.detailUI.getChampBE();
    }

    public JButton getChampRP() {
        return this.detailUI.getChampRP();
    }

    public JButton getChampBESkinRP() {
        return this.detailUI.getChampBESkinRP();
    }

    public JButton getChampRPSkinRP() {
        return this.detailUI.getChampRPSkinRP();
    }

    @Override
    public void onSelection(Champion champion) {
        this.lastSelectedSkin = null;
        this.detailUI.getChampRPSkinRP().setEnabled(false);
        this.detailUI.getChampBESkinRP().setEnabled(false);
        this.lastSelectedChampion = champion;
        Wallet wallet = callback.getWallet();
        this.detailUI.toggle(champion);
        this.detailUI.getChampBE().setEnabled(wallet.getBlueEssence() >= champion.getBlueEssence());
        this.detailUI.getChampRP().setEnabled(wallet.getRiotPoints() >= (champion.getRiotPoints() - champion.getSale()));
    }


    @Override
    public void onSelection(Skin skin) {
        this.lastSelectedSkin = skin;
        Wallet wallet = callback.getWallet();
        long skinCost = (skin.getCost() - skin.getSale());
        this.detailUI.getSkin().setEnabled(wallet.getRiotPoints() >= skinCost);
        if (lastSelectedChampion == null) return;
        long champCostRP = (lastSelectedChampion.getRiotPoints() - lastSelectedChampion.getSale());
        this.detailUI.getChampRPSkinRP().setEnabled(wallet.getRiotPoints() >= (skinCost + champCostRP));
        this.detailUI.getChampBESkinRP().setEnabled(wallet.getRiotPoints() >= skinCost && wallet.getBlueEssence() >= lastSelectedChampion.getBlueEssence());
    }
}
