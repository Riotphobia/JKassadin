package com.hawolt.lcu;

import com.hawolt.Call;
import com.hawolt.LeagueClient;
import com.hawolt.data.InventoryType;
import com.hawolt.data.PaymentMethod;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created: 29/07/2022 02:05
 * Author: Twitter @hawolt
 **/

public class PurchaseWidget {

    public static JSONObject buildFinalizedChampSelectWidgetPayload(JSONObject... payloads) {
        JSONObject body = new JSONObject();
        JSONArray items = new JSONArray();
        for (JSONObject payload : payloads) items.put(payload);
        body.put("items", items);
        return body;
    }

    public static JSONObject buildChampSelectWidgetPayload(InventoryType type, long itemId, PaymentMethod method, long price) {
        JSONObject object = new JSONObject();
        JSONObject itemKey = new JSONObject();
        itemKey.put("inventoryType", type.name());
        itemKey.put("itemId", itemId);
        object.put("itemKey", itemKey);
        JSONObject purchaseCurrencyInfo = new JSONObject();
        purchaseCurrencyInfo.put("currencyType", method.getName());
        purchaseCurrencyInfo.put("price", price);
        purchaseCurrencyInfo.put("purchasable", true);
        object.put("purchaseCurrencyInfo", purchaseCurrencyInfo);
        object.put("source", "champSelect");
        object.put("quantity", 1);
        return object;
    }

    public static JSONObject buildSingletonPayload(InventoryType type, PaymentMethod method, long itemId, long price) {
        JSONObject item = new JSONObject();
        item.put("inventoryType", type.name());
        item.put("itemId", itemId);
        if (method == PaymentMethod.BLUE_ESSENCE) item.put("ipCost", price);
        if (method == PaymentMethod.RIOT_POINTS) item.put("rpCost", price);
        item.put("quantity", 1);
        return item;
    }

    public static JSONObject submit(LeagueClient client, JSONObject payload) throws IOException {
        String endpoint = String.format("https://127.0.0.1:%s/lol-purchase-widget/v2/purchaseItems", client.getPort());
        Request request = new Call(client, endpoint, Method.POST, true);
        request.addHeader("X-Riot-Source", "rcp-fe-lol-paw");
        request.write(payload.toString());
        Response response = request.execute();
        Logger.debug("/lol-purchase-widget/v2/purchaseItems {}", response.getCode());
        return new JSONObject(response.getBodyAsString());
    }
}
