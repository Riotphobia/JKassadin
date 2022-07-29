package com.hawolt;

import java.awt.*;

/**
 * Created: 20/07/2022 09:58
 * Author: Twitter @hawolt
 **/

public interface Module {

    void onUpdate(LeagueClient client, SummonerProfile profile);

    boolean isBackground();

    void onUpdate();

    void onExit();
}
