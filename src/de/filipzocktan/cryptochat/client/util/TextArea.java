package de.filipzocktan.cryptochat.client.util;

public class TextArea extends javafx.scene.control.TextArea {

    public void appendLine(String text){
        appendText(text + "\n");
    }

    public void newMessage(String Username, String text){
        appendLine("<" + Username + "> " + text);
    }

}
