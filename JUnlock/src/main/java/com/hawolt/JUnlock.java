package com.hawolt;

import com.hawolt.data.*;
import com.hawolt.http.*;
import com.hawolt.impl.ItemModule;
import com.hawolt.lcu.ChampionSelect;
import com.hawolt.lcu.PurchaseWidget;
import com.hawolt.lcu.Telemetry;
import com.hawolt.logger.Logger;
import com.hawolt.ui.JUnlockUI;
import com.hawolt.ui.callback.IValueCallback;
import com.hawolt.ui.callback.WalletCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created: 20/07/2022 14:18
 * Author: Twitter @hawolt
 **/

public class JUnlock extends ItemModule implements WalletCallback, IValueCallback, Runnable {

    private final static HashMap<String, Champion> cache = new HashMap<>();
    private final ScheduledFuture<?> future;

    private Wallet wallet = new Wallet(0, 0);
    private RegionLocale locale;
    private JUnlockUI unlockUI;
    private Session session;

    public JUnlock(String name) {
        super(name);
        this.future = Application.service.scheduleWithFixedDelay(this, 0, 1, TimeUnit.SECONDS);
        Application.service.execute(() -> {
            try {
                Request request = new Request("https://cdn.merakianalytics.com/riot/lol/resources/latest/en-US/champions.json");
                Response response = request.execute();
                JSONObject object = new JSONObject(response.getBodyAsString());
                for (String key : object.keySet()) {
                    try {
                        JSONObject champion = object.getJSONObject(key);
                        String championString = champion.getString("name");
                        cache.put(championString, new Champion(championString, champion));
                    } catch (Exception e) {
                        Logger.error(e);
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        });
        this.addActionListener(listener -> {
            if (unlockUI != null && unlockUI.isVisible()) return;
            unlockUI = new JUnlockUI(this, "JUnlock", cache);
            unlockUI.getSkin().addActionListener(action -> Application.service.execute(() -> {
                try {
                    Skin skin = unlockUI.getDetailUI().getCurrent().getSkinUI().getSelectedSkin();
                    Telemetry.startTelemetryFlow(client);
                    JSONObject base = PurchaseWidget.buildChampSelectWidgetPayload(InventoryType.CHAMPION_SKIN, skin.getId(), PaymentMethod.RIOT_POINTS, (skin.getCost() - skin.getSale()));
                    JSONObject payload = PurchaseWidget.buildFinalizedChampSelectWidgetPayload(base);
                    Telemetry.startTelemetryPurchase(client, payload);
                    JSONObject details = PurchaseWidget.submit(client, payload);
                    Telemetry.finishTelemetryPurchase(client, details);
                    ChampionSelect.refreshInventory(client);
                    Thread.sleep(2000L);
                    ChampionSelect.selectSkin(client, skin.getId());
                    Thread.sleep(1000L);
                    Storefront.loadAndCacheTransactionHistory(session, locale);
                    Storefront.refundLastTransaction(session, locale, profile);
                } catch (Exception e) {
                    Logger.error(e);
                    Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
                }
            }));
            unlockUI.getChampRP().addActionListener(action -> Application.service.execute(() -> {
                try {
                    Champion champion = unlockUI.getDetailUI().getChampion();
                    long accountId = profile.getAccountId();
                    JSONObject payload = Storefront.buildStorePayload(accountId, InventoryType.CHAMPION, champion.getId(), PaymentMethod.RIOT_POINTS, (champion.getRiotPoints() - champion.getSale()));
                    Storefront.purchase(session, locale, payload);
                    ChampionSelect.refreshInventory(client);
                    Thread.sleep(2000L);
                    ChampionSelect.selectChampion(client, profile, champion.getId());
                    Thread.sleep(1000L);
                    Storefront.loadAndCacheTransactionHistory(session, locale);
                    Storefront.refundLastTransaction(session, locale, profile);
                } catch (Exception e) {
                    Logger.error(e);
                    Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
                }
            }));
            unlockUI.getChampBE().addActionListener(action -> Application.service.execute(() -> {
                try {
                    Champion champion = unlockUI.getDetailUI().getChampion();
                    long accountId = profile.getAccountId();
                    JSONObject payload = Storefront.buildStorePayload(accountId, InventoryType.CHAMPION, champion.getId(), PaymentMethod.BLUE_ESSENCE, champion.getBlueEssence());
                    Storefront.purchase(session, locale, payload);
                    ChampionSelect.refreshInventory(client);
                    Thread.sleep(2000L);
                    ChampionSelect.selectChampion(client, profile, champion.getId());
                    Thread.sleep(1000L);
                    Storefront.loadAndCacheTransactionHistory(session, locale);
                    Storefront.refundLastTransaction(session, locale, profile);
                } catch (Exception e) {
                    Logger.error(e);
                    Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
                }
            }));
            unlockUI.getChampBESkinRP().addActionListener(action -> Application.service.execute(() -> {
                try {
                    Champion champion = unlockUI.getDetailUI().getChampion();
                    Skin skin = unlockUI.getDetailUI().getCurrent().getSkinUI().getSelectedSkin();
                    Telemetry.startTelemetryFlow(client);
                    JSONObject base1 = PurchaseWidget.buildChampSelectWidgetPayload(InventoryType.CHAMPION, champion.getId(), PaymentMethod.BLUE_ESSENCE, champion.getBlueEssence());
                    JSONObject base2 = PurchaseWidget.buildChampSelectWidgetPayload(InventoryType.CHAMPION_SKIN, skin.getId(), PaymentMethod.RIOT_POINTS, (skin.getCost() - skin.getSale()));
                    JSONObject payload = PurchaseWidget.buildFinalizedChampSelectWidgetPayload(base1, base2);
                    Telemetry.startTelemetryPurchase(client, payload);
                    JSONObject details = PurchaseWidget.submit(client, payload);
                    Telemetry.finishTelemetryPurchase(client, details);
                    ChampionSelect.refreshInventory(client);
                    Thread.sleep(2000L);
                    ChampionSelect.selectChampion(client, profile, champion.getId());
                    ChampionSelect.selectSkin(client, skin.getId());
                    Thread.sleep(1000L);
                    Storefront.loadAndCacheTransactionHistory(session, locale);
                    Storefront.refundLastTransaction(session, locale, profile);
                    Storefront.refundLastTransaction(session, locale, profile);
                } catch (Exception e) {
                    Logger.error(e);
                    Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
                }
            }));
            unlockUI.getChampRPSkinRP().addActionListener(action -> Application.service.execute(() -> {
                try {
                    Champion champion = unlockUI.getDetailUI().getChampion();
                    Skin skin = unlockUI.getDetailUI().getCurrent().getSkinUI().getSelectedSkin();
                    Telemetry.startTelemetryFlow(client);
                    JSONObject base1 = PurchaseWidget.buildChampSelectWidgetPayload(InventoryType.CHAMPION, champion.getId(), PaymentMethod.RIOT_POINTS, (champion.getRiotPoints() - champion.getSale()));
                    JSONObject base2 = PurchaseWidget.buildChampSelectWidgetPayload(InventoryType.CHAMPION_SKIN, skin.getId(), PaymentMethod.RIOT_POINTS, (skin.getCost() - skin.getSale()));
                    JSONObject payload = PurchaseWidget.buildFinalizedChampSelectWidgetPayload(base1, base2);
                    Telemetry.startTelemetryPurchase(client, payload);
                    JSONObject details = PurchaseWidget.submit(client, payload);
                    Telemetry.finishTelemetryPurchase(client, details);
                    ChampionSelect.refreshInventory(client);
                    Thread.sleep(2000L);
                    ChampionSelect.selectChampion(client, profile, champion.getId());
                    ChampionSelect.selectSkin(client, skin.getId());
                    Thread.sleep(1000L);
                    Storefront.loadAndCacheTransactionHistory(session, locale);
                    Storefront.refundLastTransaction(session, locale, profile);
                    Storefront.refundLastTransaction(session, locale, profile);
                } catch (Exception e) {
                    Logger.error(e);
                    Application.notification("JUnlock", e.getMessage(), TrayIcon.MessageType.ERROR);
                }
            }));
        });
    }

    @Override
    public boolean isBackground() {
        return false;
    }

    @Override
    public void onUpdate() {
        this.setEnabled(client != null);
        if (this.unlockUI != null && this.client == null) {
            unlockUI.dispose();
        }
        this.session = null;
        this.locale = null;
        if (client == null) return;
        Application.service.execute(() -> {
            try {
                String endpoint = String.format("https://127.0.0.1:%s/riotclient/get_region_locale", client.getPort());
                Request request = new Call(client, endpoint, Method.GET, false);
                Response response = request.execute();
                this.locale = new RegionLocale(new JSONObject(response.getBodyAsString()));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
        Application.service.execute(() -> {
            try {
                String endpoint = String.format("https://127.0.0.1:%s/lol-login/v1/session", client.getPort());
                Request request = new Call(client, endpoint, Method.GET, false);
                Response response = request.execute();
                this.session = new Session(new JSONObject(response.getBodyAsString()));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
    }

    @Override
    public void onExit() {
        if (unlockUI != null && unlockUI.isVisible()) unlockUI.dispose();
        if (future != null && !future.isCancelled()) future.cancel(true);
    }

    @Override
    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public void run() {
        this.setEnabled(!(this.client == null));
        if (!isEnabled()) return;
        try {
            String endpoint = String.format("https://127.0.0.1:%s/lol-store/v1/wallet", client.getPort());
            Request request = new Call(client, endpoint, Method.GET, false);
            Response response = request.execute();
            JSONObject object = new JSONObject(response.getBodyAsString());
            this.wallet = new Wallet(object.getLong("ip"), object.getLong("rp"));
            if (this.unlockUI != null) this.unlockUI.getWalletUI().update(wallet);
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    @Override
    public SummonerProfile getSummonerProfile() {
        return profile;
    }

    @Override
    public RegionLocale getLocale() {
        return locale;
    }

    @Override
    public Session getSession() {
        return session;
    }

    public static Optional<String> retrieveFromChampion(long itemId) {
        return cache.values().stream().filter(champion -> champion.getId() == itemId).map(Champion::getName).findFirst();
    }

    public static Optional<String> retrieveFromSkin(long itemId) {
        return cache.values().stream().flatMap(champion -> champion.getList().stream()).filter(skin -> skin.getId() == itemId).map(Skin::toString).findFirst();
    }
}
