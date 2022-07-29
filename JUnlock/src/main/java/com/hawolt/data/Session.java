package com.hawolt;

import org.json.JSONObject;

/**
 * Created: 20/07/2022 14:33
 * Author: Twitter @hawolt
 **/

public class Session {
    private final String idToken;

    public Session(JSONObject object) {
        this.idToken = object.getString("idToken");
    }

    public String getIdToken() {
        return idToken;
    }
}
