package de.filipzocktan.cryptochat.client.sockets;

import de.filipzocktan.cryptochat.client.CryptoChatClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class StatusSocket extends Socket {

    StatusSocket(String host, int port) throws IOException {
        super(host, port);
    }

    public void sendDisconnect() {
        PrintWriter out = CryptoChatClient.sockets.getStatusOut();
        out.print("DISCONNECT\n");
        out.flush();
    }


}
