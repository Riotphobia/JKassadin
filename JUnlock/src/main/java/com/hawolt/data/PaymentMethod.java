package com.hawolt.data;

/**
 * Created: 27/07/2022 21:31
 * Author: Twitter @hawolt
 **/

public enum PaymentMethod {
    BLUE_ESSENCE("IP"), RIOT_POINTS("RP");
    final String name;

    PaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
