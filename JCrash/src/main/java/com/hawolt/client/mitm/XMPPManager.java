package com.hawolt.client.mitm;

import com.hawolt.Application;
import com.hawolt.Configuration;
import com.hawolt.logger.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Created: 16/06/2022 07:50
 * Author: Twitter @hawolt
 **/

public class XMPPManager {

    private static Socket socket, local;

    public static void start() {
        if (socket != null && !socket.isClosed()) return;
        try {
            String store = Configuration.get("trustStore");
            int port = Integer.parseInt(Configuration.get("chat.port"));
            char[] password = Configuration.get("trustStorePassword").toCharArray();
            TLSServer server = new TLSServer(port, "TLSv1.2", store, password, store, password);
            XMPPManager.socket = SSLSocketFactory.getDefault().createSocket(Configuration.get("host"), Integer.parseInt(Configuration.get("chat.port")));
            XMPPManager.local = server.accept();
            Application.service.execute(new Incoming(XMPPManager.socket, XMPPManager.local, () -> {
                try {
                    server.shutdown();
                } catch (IOException e) {
                    Logger.error(e);
                }
            }));
            Application.service.execute(new Outgoing(XMPPManager.local, XMPPManager.socket));
        } catch (Exception e) {
            Logger.error(e);
        }
    }


}
