package com.hawolt;

/**
 * Created: 19/07/2022 19:44
 * Author: Twitter @hawolt
 **/

public class LeagueClient {
    private final String auth, port;

    public LeagueClient(String auth, String port) {
        this.auth = auth;
        this.port = port;
    }

    public String getAuth() {
        return auth;
    }

    public String getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "LeagueClient{" +
                "auth='" + auth + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
