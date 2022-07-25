package com.hawolt.client.mitm;

import com.hawolt.io.Core;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created: 10/04/2022 00:36
 * Author: Twitter @hawolt
 **/

public class TLSServer {

    private final SSLServerSocket server;

    public TLSServer(int port, String version, String trustStoreName, char[] trustStorePassword, String keyStoreName, char[] keyStorePassword) throws Exception {
        if (port <= 0) throw new IllegalArgumentException("Port number cannot be less than or equal to 0");
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream stream = Core.getResourceAsStream(trustStoreName)) {
            trustStore.load(stream, trustStorePassword);
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream stream = Core.getResourceAsStream(keyStoreName)) {
            keyStore.load(stream, keyStorePassword);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), SecureRandom.getInstanceStrong());
        SSLServerSocketFactory factory = ctx.getServerSocketFactory();
        server = (SSLServerSocket) factory.createServerSocket(port);
        server.setNeedClientAuth(false);
        server.setEnabledProtocols(new String[]{version});
    }

    public Socket accept() throws IOException {
        return server.accept();
    }

    public void shutdown() throws IOException {
        server.close();
    }
}