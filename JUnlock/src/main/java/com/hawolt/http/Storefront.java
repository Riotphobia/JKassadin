package com.hawolt.http;

import com.hawolt.Application;
import com.hawolt.RegionLocale;
import com.hawolt.SummonerProfile;
import com.hawolt.data.*;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import com.hawolt.ui.JUnlockRefundUI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created: 29/07/2022 00:35
 * Author: Twitter @hawolt
 **/

public class Storefront {

    private final static List<Transaction> list = new LinkedList<>();
    private final static Object lock = new Object();

    public static void remove(Transaction transaction) {
        synchronized (lock) {
            Storefront.list.remove(transaction);
            JUnlockRefundUI.refresh(list);
        }
    }

    private static boolean contains(long itemId) {
        for (Transaction o : list) {
            if (o.getItemId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public static void loadAndCacheTransactionHistory(Session session, RegionLocale locale) throws IOException {
        String endpoint = String.format("https://%s.store.leagueoflegends.com/storefront/v3/history/purchase", StoreLocale.retrieve(locale.getWebRegion()));
        Request request = new Request(endpoint, Method.GET, false);
        request.addHeader("Authorization", "Bearer " + session.getIdToken());
        Response response = request.execute();
        JSONObject history = new JSONObject(response.getBodyAsString());
        JSONArray transactions = history.getJSONArray("transactions");
        synchronized (lock) {
            Storefront.list.clear();
            for (int i = 0; i < transactions.length(); i++) {
                try {
                    Transaction transaction = new Transaction(transactions.getJSONObject(i));
                    if (transaction.getInventoryType().equals("HEXTECH_CRAFTING")) continue;
                    if (transaction.getAmountSpent() == 0) continue;
                    if (transaction.getName() == null) continue;
                    if (transaction.isRefunded()) continue;
                    if (contains(transaction.getItemId())) continue;
                    list.add(transaction);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.println(transactions.getJSONObject(i));
                }
            }
            JUnlockRefundUI.refresh(list);
        }
    }

    public static void refund(Session session, RegionLocale locale, SummonerProfile profile, Transaction transaction) {
        JSONObject payload = new JSONObject();
        payload.put("accountId", profile.getAccountId());
        payload.put("transactionId", transaction.getTransactionId());
        payload.put("inventoryType", transaction.getInventoryType());
        payload.put("language", locale.getWebLanguage());
        try {
            refund(session, locale, payload);
            synchronized (lock) {
                remove(transaction);
                JUnlockRefundUI.refresh(list);
            }
        } catch (Exception e) {
            Logger.error(e);
            Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

    public static void refundLastTransaction(Session session, RegionLocale locale, SummonerProfile profile) {
        refundLastTransaction(session, locale, profile, 0);
    }

    public static void refundLastTransaction(Session session, RegionLocale locale, SummonerProfile profile, int offset) {
        synchronized (lock) {
            if (list.isEmpty()) {
                Application.notification("JUnlock", "No Transactions available", TrayIcon.MessageType.ERROR);
            } else {
                refund(session, locale, profile, list.get(list.size() - 1 - offset));
            }
        }
    }

    private static void refund(Session session, RegionLocale locale, JSONObject payload) throws IOException {
        String endpoint = String.format("https://%s.store.leagueoflegends.com/storefront/v3/refund", StoreLocale.retrieve(locale.getWebRegion()));
        Request request = new StoreRequest(endpoint, session, payload.toString());
        System.out.println(payload);
        request.write(payload.toString());
        Response response = request.execute();
        if (response.getCode() == 200) {
            Application.notification("JUnlock", "Refunded transaction", TrayIcon.MessageType.INFO);
        } else {
            System.err.println(response.getCode() + ": " + response.getBodyAsString());
            Application.notification("JUnlock", "Failed to refund transaction", TrayIcon.MessageType.ERROR);
        }
    }

    public static JSONObject buildStorePayload(long accountId, InventoryType type, long itemId, PaymentMethod method, long price) {
        JSONObject object = new JSONObject();
        object.put("accountId", accountId);
        JSONArray items = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("quantity", 1);
        item.put("itemId", itemId);
        item.put("inventoryType", type.name());
        item.put("rpCost", method == PaymentMethod.RIOT_POINTS ? price : JSONObject.NULL);
        item.put("ipCost", method == PaymentMethod.BLUE_ESSENCE ? price : JSONObject.NULL);
        items.put(item);
        object.put("items", items);
        return object;
    }

    public static void purchase(Session session, RegionLocale locale, JSONObject payload) {
        String endpoint = String.format("https://%s.store.leagueoflegends.com/storefront/v3/purchase", StoreLocale.retrieve(locale.getWebRegion()));
        try {
            Request request = new StoreRequest(endpoint, session, payload.toString());
            request.write(payload.toString());
            Response response = request.execute();
            Logger.debug("/storefront/v3/purchase {}", response.getCode());
        } catch (IOException e) {
            Logger.error(e);
            Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
        }
    }

}
