package de.filipzocktan.cryptochat.client;

import de.filipzocktan.cryptochat.client.frames.ChatFrame;
import de.filipzocktan.cryptochat.client.sockets.SocketCollection;
import de.filipzocktan.util.crypto.Crypto;
import io.sentry.Sentry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

public class CryptoChatClient {

    public static Crypto crypto;
    public static SocketCollection sockets;
    private static ChatFrame chatFrame;
    public static boolean connected = false;
    private static boolean running = true;

    public static void main(String[] args) {
        Sentry.init();
        crypto = new Crypto();
        chatFrame = new ChatFrame();
        ChatFrame.main(null);
    }

    public static void startServices() {
        new ChatService().start();
        new StatusService().start();
        new UserService().start();
        new KeyService().start();
    }

    public static void login(String host, int port, String username, String password) {
        System.out.println("Logging in as " + username + ":" + password + "@" + host + ":" + port);
        connected = true;
        try {
            sockets = new SocketCollection(host, port);
        } catch (IOException e) {
            connected = false;
            e.printStackTrace();
            Sentry.capture(e);
        }
        if (!sockets.isSOpened()) {
            connected = false;
            return;
        }
        sockets.getUserSocket().login(username, password);
        sockets.getKeyOut().write(new String(Base64.getEncoder().encode(crypto.getPubKey().getEncoded())) + "\n");
        sockets.getKeyOut().flush();
    }

    private static class ChatService extends Thread {

        @Override
        public void run() {
            while (running) {
                try {
                    sockets.getChatSocket().readMessages();
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                        Sentry.capture(e);
                    }
                }
            }
            interrupt();
        }
    }

    private static class KeyService extends Thread {

        @Override
        public void run() {
            while (running) {
                try {
                    String input = sockets.getKeyIn().readLine();
                    crypto.setServerKey(Base64.getDecoder().decode(input));
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                        Sentry.capture(e);
                    }
                }
            }
            interrupt();
        }
    }

    private static class UserService extends Thread {
        @Override
        public void run() {
            try {
                while (running) {
                    String cmd = sockets.getUserIn().readLine();
                    switch (cmd) {
                        default:
                            String[] args = cmd.split(";;");
                            switch (args[0]) {
                                case "LOGINANSWER":
                                    switch (args[1]) {
                                        case "WRONGPASSWORD":
                                            connected = false;
                                            chatFrame.getChat().appendLine("Wrong password.");
                                            sockets.close();
                                            running = false;
                                            break;
                                        case "WRONGUSER":
                                            connected = false;
                                            chatFrame.getChat().appendLine("The user doesn't exist on this server.");
                                            sockets.close();
                                            running = false;
                                            break;
                                        case "USERONLINE":
                                            connected = false;
                                            chatFrame.getChat().appendLine("You are already logged in.");
                                            sockets.close();
                                            running = false;
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                }
                interrupt();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }
        }
    }

    static class StatusService extends Thread {

        BufferedReader in;
        PrintWriter out;

        public void run() {
            try {
                in = CryptoChatClient.sockets.getStatusIn();
                out = CryptoChatClient.sockets.getStatusOut();

                while (running) {
                    String cmd = in.readLine();
                    if (cmd == null) {
                        return;
                    }
                    switch (cmd) {
                        case "HEARTBEAT":
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    out.print("HEARTBEAT\n");
                                    out.flush();
                                }
                            }, 5000);
                            break;
                        case "SERVERCLOSED":
                            connected = false;
                            chatFrame.getChat().appendLine("Server closed.");
                            sockets.close();
                            running = false;
                            break;
                    }
                }
                interrupt();
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }
        }
    }
}