package com.hawolt.data;

import com.hawolt.JUnlock;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created: 29/07/2022 00:42
 * Author: Twitter @hawolt
 **/

public class Transaction {

    private final String currencyType, inventoryType, datePurchased, transactionId, name;
    private final boolean refundable, refunded;
    private final long itemId, amountSpent;

    public Transaction(JSONObject object) {
        this.refunded = object.has("refundabilityMessage") && object.getString("refundabilityMessage").equals("ALREADY_REFUNDED");
        this.datePurchased = format(object.getString("datePurchased"));
        this.inventoryType = object.getString("inventoryType");
        this.transactionId = object.getString("transactionId");
        this.currencyType = object.getString("currencyType");
        this.refundable = object.getBoolean("refundable");
        this.amountSpent = object.getLong("amountSpent");
        this.itemId = object.getLong("itemId");
        this.name = JUnlock.retrieveFromChampion(this.itemId).orElseGet(() -> JUnlock.retrieveFromSkin(itemId).orElse(null));
    }

    private String format(String date) {
        String[] values = date.split("/");
        return String.join("/",
                String.format("%02d", Integer.parseInt(values[0])),
                String.format("%02d", Integer.parseInt(values[1])),
                String.format("%02d", Integer.parseInt(values[2]))
        );
    }

    public String getName() {
        return name;
    }

    public boolean isRefunded() {
        return refunded;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public String getDatePurchased() {
        return datePurchased;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public long getItemId() {
        return itemId;
    }

    public long getAmountSpent() {
        return amountSpent;
    }

    @Override
    public String toString() {
        return String.join(" - ", datePurchased, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return refundable == that.refundable && refunded == that.refunded && itemId == that.itemId && amountSpent == that.amountSpent && Objects.equals(currencyType, that.currencyType) && Objects.equals(inventoryType, that.inventoryType) && Objects.equals(datePurchased, that.datePurchased) && Objects.equals(transactionId, that.transactionId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyType, inventoryType, datePurchased, transactionId, name, refundable, refunded, itemId, amountSpent);
    }
}
