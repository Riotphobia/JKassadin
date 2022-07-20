package com.hawolt;

import java.awt.*;

/**
 * Created: 20/07/2022 09:58
 * Author: Twitter @hawolt
 **/

public abstract class Module extends PopupMenu implements IAccountSelection {

    protected SummonerProfile profile;
    protected LeagueClient client;

    @Override
    public void onUpdate(LeagueClient client, SummonerProfile profile) {
        this.profile = profile;
        this.client = client;
        this.onUpdate();
    }

    public Module(String name) {
        super(name);
        this.setEnabled(false);
    }

    abstract boolean isBackground();

    abstract void onUpdate();

    abstract void onExit();
}
