package de.filipzocktan.cryptochat.client.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SocketCollection {

    //ChatSocket (given port)
    private final ChatSocket chatSocket;
    private final PrintWriter chatOut;
    private final BufferedReader chatIn;
    
    //UserSocket (given port+1)
    private final UserSocket userSocket;
    private final PrintWriter userOut;
    private final BufferedReader userIn;
    
    //KeySocket (given port+2)
    private final KeySocket keySocket;
    private final PrintWriter keyOut;
    private final BufferedReader keyIn;
    
    //KeySocket (given port+3)
    private final StatusSocket statusSocket;
    private final PrintWriter statusOut;
    private final BufferedReader statusIn;
    
    private final boolean socketsOpened;

    public SocketCollection(String host, int port) throws IOException {
        BufferedReader userIn1;
        PrintWriter userOut1;
        PrintWriter chatOut1;
        BufferedReader chatIn1;
        BufferedReader keyIn1;
        PrintWriter keyOut1;
        PrintWriter statusOut1;
        BufferedReader statusIn1;
        chatSocket = createChatSocket(host, port);
        userSocket = createUserSocket(host, port);
        keySocket = createKeySocket(host, port);
        statusSocket = createStatusSocket(host, port);

        try {
            chatIn1 = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
            chatOut1 = new PrintWriter(chatSocket.getOutputStream());
        } catch (Exception ex){
            chatIn1 =null;
            chatOut1 =null;
            ex.printStackTrace();
        }

        try {
            userOut1 =new PrintWriter(userSocket.getOutputStream());
            userIn1 = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        } catch(Exception ex){
            userIn1 = null;
            userOut1 =null;
            ex.printStackTrace();
        }

        try {
            statusIn1 = new BufferedReader(new InputStreamReader(statusSocket.getInputStream()));
            statusOut1 = new PrintWriter(statusSocket.getOutputStream());
        } catch (Exception ex){
            statusIn1 =null;
            statusOut1 =null;
            ex.printStackTrace();
        }

        try {
            keyOut1 =new PrintWriter(keySocket.getOutputStream());
            keyIn1 = new BufferedReader(new InputStreamReader(keySocket.getInputStream()));
        } catch(Exception ex){
            keyIn1 = null;
            keyOut1 =null;
            ex.printStackTrace();
        }

        userIn = userIn1;
        userOut = userOut1;
        chatOut = chatOut1;
        chatIn = chatIn1;
        keyIn=keyIn1;
        keyOut=keyOut1;
        statusIn=statusIn1;
        statusOut=statusOut1;

        if (keySocket == null) {
            socketsOpened = false;
        } else if (chatSocket == null) {
            socketsOpened = false;
        } else if (userSocket == null) {
            socketsOpened = false;

        } else if (statusSocket == null) {
            socketsOpened = false;
        } else {
            socketsOpened = true;
        }

    }

    public ChatSocket getChatSocket() {
        return chatSocket;
    }

    public UserSocket getUserSocket() {
        return userSocket;
    }

    public KeySocket getKeySocket() {
        return keySocket;
    }

    public StatusSocket getStatusSocket() { return statusSocket;}

    public boolean isSOpened() {
        return socketsOpened;
    }

    private StatusSocket createStatusSocket(String host, int port) throws IOException {
            return new StatusSocket(host, port+3);
    }

    private ChatSocket createChatSocket(String host, int port) throws IOException {
            return new ChatSocket(host, port);
    }

    private UserSocket createUserSocket(String host, int port) throws IOException {
            return new UserSocket(host, port + 1);
    }

    private KeySocket createKeySocket(String host, int port) throws IOException {
            return new KeySocket(host, port + 2);
    }

    public PrintWriter getChatOut() {
        return chatOut;
    }

    public BufferedReader getChatIn() {
        return chatIn;
    }

    public PrintWriter getUserOut() {
        return userOut;
    }

    public BufferedReader getUserIn() {
        return userIn;
    }

    public PrintWriter getKeyOut() {
        return keyOut;
    }

    public BufferedReader getKeyIn() {
        return keyIn;
    }

    public PrintWriter getStatusOut() {
        return statusOut;
    }

    public BufferedReader getStatusIn() {
        return statusIn;
    }
    
    public void close(){
        try {
            getChatIn().close();
            getChatOut().close();
            getChatSocket().close();
            getUserIn().close();
            getUserOut().close();
            getUserSocket().close();
            getKeyIn().close();
            getKeyOut().close();
            getKeySocket().close();
            getStatusIn().close();
            getStatusOut().close();
            getStatusSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
