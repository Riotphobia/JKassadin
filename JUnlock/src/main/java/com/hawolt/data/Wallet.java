package com.hawolt.data;

/**
 * Created: 27/07/2022 20:46
 * Author: Twitter @hawolt
 **/

public class Wallet {
    private final long be, rp;

    public Wallet(long be, long rp) {
        this.be = be;
        this.rp = rp;
    }

    public long getRiotPoints() {
        return rp;
    }

    public long getBlueEssence() {
        return be;
    }
}
