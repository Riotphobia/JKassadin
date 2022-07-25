package com.hawolt.client.mitm;

import com.hawolt.client.StreamCallback;

import java.net.Socket;

/**
 * Created: 16/06/2022 08:05
 * Author: Twitter @hawolt
 **/

public abstract class Connection implements Runnable {

    protected final StreamCallback callback;
    protected final Socket in, out;

    public Connection(Socket in, Socket out) {
        this(in, out, null);
    }

    public Connection(Socket in, Socket out, StreamCallback callback) {
        this.callback = callback;
        this.out = out;
        this.in = in;
    }
}
