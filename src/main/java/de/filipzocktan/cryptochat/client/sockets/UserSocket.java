package de.filipzocktan.cryptochat.client.sockets;

import io.sentry.Sentry;

import java.io.IOException;
import java.net.Socket;

public class UserSocket extends Socket {

    UserSocket(String host, int port) throws IOException {
        super(host, port);
    }

    public void login(String username, String password) {
        try {
            getOutputStream().write(("LOGIN;;" + username + ";;" + password + "\n").getBytes());
            getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
    }

}
