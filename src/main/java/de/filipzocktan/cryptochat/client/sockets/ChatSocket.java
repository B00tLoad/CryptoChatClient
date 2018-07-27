package de.filipzocktan.cryptochat.client.sockets;

import de.filipzocktan.cryptochat.client.CryptoChatClient;
import de.filipzocktan.cryptochat.client.frames.ChatFrame;
import io.sentry.Sentry;

import java.io.IOException;
import java.net.Socket;
import java.util.Base64;

public class ChatSocket extends Socket {
    ChatSocket(String host, int port) throws IOException {
        super(host, port);
    }

    public void readMessages() throws IOException {
        String input = CryptoChatClient.sockets.getChatIn().readLine();
        if (input == null) return;
        if ("".equals(input)) return;
        while (true) {
            if (CryptoChatClient.crypto.hasServerKey()) break;
        }
        String msg = "";
        try {
            msg = new String(CryptoChatClient.crypto.decrypt(Base64.getDecoder().decode(input)));
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
        if ("".equals(msg)) return;
        ChatFrame.chat.appendLine(msg);
    }

    public void sendMessage(String message) {
        byte[] encryptedMessage = new byte[0];
        try {
            encryptedMessage = CryptoChatClient.crypto.encrypt(message);
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
        CryptoChatClient.sockets.getChatOut().write(new String(Base64.getEncoder().encode(encryptedMessage)) + "\n");
        CryptoChatClient.sockets.getChatOut().flush();
    }
}