package com.hawolt;

import org.json.JSONObject;

/**
 * Created: 20/07/2022 09:42
 * Author: Twitter @hawolt
 **/

public class SummonerProfile {
    private final String displayName, puuid;
    private final long accountId, summonerId;

    public SummonerProfile(JSONObject object) {
        this.displayName = object.getString("displayName");
        this.puuid = object.getString("puuid");
        this.accountId = object.getLong("accountId");
        this.summonerId = object.getLong("summonerId");
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPUUID() {
        return puuid;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getSummonerId() {
        return summonerId;
    }

    @Override
    public String toString() {
        return "SummonerProfile{" +
                "displayName='" + displayName + '\'' +
                ", puuid='" + puuid + '\'' +
                ", accountId=" + accountId +
                ", summonerId=" + summonerId +
                '}';
    }
}
