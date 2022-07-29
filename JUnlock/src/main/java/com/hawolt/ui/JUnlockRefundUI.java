package com.hawolt.ui;

import com.hawolt.Application;
import com.hawolt.data.Transaction;
import com.hawolt.http.Storefront;
import com.hawolt.logger.Logger;
import com.hawolt.ui.callback.IValueCallback;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Created: 29/07/2022 00:31
 * Author: Twitter @hawolt
 **/

public class JUnlockRefundUI extends JPanel {

    private final static JComboBox<Transaction> transactionJComboBox = new JComboBox<>();

    public JUnlockRefundUI(IValueCallback callback) {
        this.setLayout(new GridLayout(0, 1, 0, 5));
        this.setBorder(BorderFactory.createTitledBorder("Transactions"));
        this.add(JUnlockRefundUI.transactionJComboBox);
        JPanel main = new JPanel(new GridLayout(0, 2, 5, 0));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(listener -> {
            refresh.setEnabled(false);
            Application.service.execute(() -> {
                try {
                    Storefront.loadAndCacheTransactionHistory(callback.getSession(), callback.getLocale());
                } catch (Exception e) {
                    Logger.error(e);
                }
                refresh.setEnabled(true);
            });
        });
        main.add(refresh, BorderLayout.NORTH);
        JButton refund = new JButton("Refund");
        refund.addActionListener(listener -> {
            refund.setEnabled(false);
            Application.service.execute(() -> {
                Transaction transaction = JUnlockRefundUI.transactionJComboBox.getItemAt(JUnlockRefundUI.transactionJComboBox.getSelectedIndex());
                if (transaction == null) {
                    Application.notification("JUnlock", "No transaction available", TrayIcon.MessageType.ERROR);
                } else {
                    Storefront.refund(callback.getSession(), callback.getLocale(), callback.getSummonerProfile(), transaction);
                }
                refund.setEnabled(true);
            });
        });
        main.add(refund, BorderLayout.SOUTH);
        this.add(main);
    }

    public static void refresh(List<Transaction> list) {
        JUnlockRefundUI.transactionJComboBox.removeAllItems();
        for (Transaction transaction : list) {
            JUnlockRefundUI.transactionJComboBox.addItem(transaction);
        }
    }
}
