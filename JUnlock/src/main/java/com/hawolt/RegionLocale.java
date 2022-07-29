package com.hawolt;

import org.json.JSONObject;

/**
 * Created: 20/07/2022 14:30
 * Author: Twitter @hawolt
 **/

public class RegionLocale {
    private final String locale, region, webLanguage, webRegion;

    public RegionLocale(JSONObject object) {
        this.locale = object.getString("locale");
        this.region = object.getString("region");
        this.webLanguage = object.getString("webLanguage");
        String webRegion = object.getString("webRegion");
        this.webRegion = (webRegion == null || webRegion.length() == 0) ? this.region : webRegion;
    }

    public String getLocale() {
        return locale;
    }

    public String getRegion() {
        return region;
    }

    public String getWebLanguage() {
        return webLanguage;
    }

    public String getWebRegion() {
        return webRegion;
    }
}
