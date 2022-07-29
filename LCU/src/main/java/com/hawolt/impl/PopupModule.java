package com.hawolt.impl;

import com.hawolt.IAccountSelection;
import com.hawolt.LeagueClient;
import com.hawolt.Module;
import com.hawolt.SummonerProfile;

import java.awt.*;

/**
 * Created: 27/07/2022 18:14
 * Author: Twitter @hawolt
 **/

public class PopupModule extends PopupMenu implements Module, IAccountSelection {
    protected SummonerProfile profile;
    protected LeagueClient client;

    public PopupModule(String name) {
        super(name);
        this.setEnabled(false);
    }

    @Override
    public void onUpdate(LeagueClient client, SummonerProfile profile) {
        this.profile = profile;
        this.client = client;
        this.onUpdate();
    }

    @Override
    public boolean isBackground() {
        return false;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onExit() {

    }
}
