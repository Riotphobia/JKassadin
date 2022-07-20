package com.hawolt;

import com.hawolt.http.Method;
import com.hawolt.http.Request;

import java.io.IOException;
import java.util.Base64;

public class Call extends Request {

    private final LeagueClient client;

    public Call(LeagueClient client, String endpoint, Method method, boolean output) throws IOException {
        super(endpoint, method, output);
        this.client = client;
        prepareRequest();
    }

    private void prepareRequest() {
        getConnection().setRequestProperty("User-Agent", "LeagueOfLegendsClient/");
        getConnection().setRequestProperty("Accept", "application/json");
        getConnection().setRequestProperty("Content-type", "application/json");
        getConnection().setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(("riot:" + client.getAuth()).getBytes()));
    }
}
