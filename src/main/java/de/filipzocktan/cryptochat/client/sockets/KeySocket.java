package de.filipzocktan.cryptochat.client.sockets;

import java.io.IOException;
import java.net.Socket;

class KeySocket extends Socket {

    KeySocket(String host, int port) throws IOException {
        super(host, port);
    }

}
