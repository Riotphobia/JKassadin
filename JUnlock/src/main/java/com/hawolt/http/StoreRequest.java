package com.hawolt.http;

import com.hawolt.data.Session;

import java.io.IOException;

/**
 * Created: 29/07/2022 01:16
 * Author: Twitter @hawolt
 **/

public class StoreRequest extends Request {
    public StoreRequest(String endpoint, Session session, String payload) throws IOException {
        super(endpoint, Method.POST, true);
        addHeader("Authorization", "Bearer " + session.getIdToken());
        addHeader("Content-Type", "application/json");
        addHeader("Content-Length", String.valueOf(payload.length()));
    }
}
