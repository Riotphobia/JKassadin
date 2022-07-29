package com.hawolt.ui.callback;

import com.hawolt.RegionLocale;
import com.hawolt.SummonerProfile;
import com.hawolt.data.Session;

/**
 * Created: 29/07/2022 01:03
 * Author: Twitter @hawolt
 **/

public interface IValueCallback {
    SummonerProfile getSummonerProfile();

    RegionLocale getLocale();

    Session getSession();
}
