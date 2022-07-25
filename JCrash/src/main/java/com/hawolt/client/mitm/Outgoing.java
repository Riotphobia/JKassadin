package com.hawolt.client.mitm;

import com.hawolt.client.global.ChampionSelect;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created: 16/06/2022 07:55
 * Author: Twitter @hawolt
 **/

public class Outgoing extends Connection {

    private final String look = "<presence id='join_muc_";

    public static final Object lock = new Object();

    public Outgoing(Socket in, Socket out) {
        super(in, out);
    }

    @Override
    public void run() {
        try (InputStream input = in.getInputStream()) {
            OutputStream stream = out.getOutputStream();
            int code;
            while ((code = input.read()) != -1) {
                byte[] b = new byte[input.available()];
                input.read(b, 0, b.length);
                String line = ((char) code) + new String(b);
                if (line.contains("join_muc") && !line.contains("post-game")) {
                    ChampionSelect.reset();
                    int mucStartIndex = line.indexOf(look);
                    int mucEndIndex = line.indexOf("'", mucStartIndex + look.length());
                    ChampionSelect.setMucId(line.substring(mucStartIndex + look.length(), mucEndIndex));
                    int selectStartIndex = line.indexOf("'", mucEndIndex + 1);
                    int selectEndIndex = line.indexOf("'", selectStartIndex + 1);
                    ChampionSelect.setSelect(line.substring(selectStartIndex + 1, selectEndIndex));
                }
                if (!line.trim().isEmpty()) Logger.debug("[OUT] {}", line);
                synchronized (Outgoing.lock) {
                    stream.write(line.getBytes());
                }
            }
        } catch (Exception e) {
            Logger.error(e);
        }
        if (callback == null) return;
        callback.onClose();
    }
}
