package de.filipzocktan.cryptochat.client.sockets;

import java.io.IOException;
import java.net.Socket;

public class KeySocket extends Socket {

    public KeySocket(String host, int port) throws IOException {
        super(host, port);
    }

}
