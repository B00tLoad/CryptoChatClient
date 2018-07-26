package de.filipzocktan.cryptochat.client.frames;

import de.filipzocktan.cryptochat.client.CryptoChatClient;
import de.filipzocktan.cryptochat.client.util.TextArea;
import io.sentry.Sentry;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.util.Stack;

import de.filipzocktan.cryptochat.client.util.TextArea;

public class ChatFrame extends Application {

    public static TextArea chat;
    Stack<String> lastMsgs = new Stack<>();
    Stack<String> messagesBefore = new Stack<>();
    Stack<String> messagesAfter = new Stack<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //Stage
        primaryStage.setTitle("CryptoChat");
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(700);

        //Gridpane
        GridPane grid = new GridPane();
//        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(20);
        grid.getColumnConstraints().addAll(col1, col2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(5);
        row1.setValignment(VPos.TOP);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(90);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(5);
        grid.getRowConstraints().addAll(row1, row2, row3);

        //Elements
        chat = new TextArea();
        chat.setEditable(false);
        grid.add(chat, 0, 1, 1, 1);

        TextField msgField = new TextField();
        msgField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        msgField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {


                switch (event.getCode()) {
                    case UP:
                        if (!messagesBefore.empty()) {
                            messagesAfter.push(msgField.getText());
                            String msg = messagesBefore.pop();
                            msgField.setText(msg);
                        }
                        break;
                    case DOWN:
                        if (!messagesAfter.empty()) {
                            messagesBefore.push(msgField.getText());
                            msgField.setText(messagesAfter.pop());
                        }
                        break;
                }
            }
        });
        grid.add(msgField, 0, 2, 1, 1);

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!msgField.getText().equals("")) {
                    try {
                        CryptoChatClient.sockets.getChatSocket().sendMessage(msgField.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Sentry.capture(e);
                    }
                    CryptoChatClient.sockets.getChatOut().flush();
                    lastMsgs.push(msgField.getText());
                    messagesBefore = lastMsgs;
                    Stack<String> messagesAfter = new Stack<>();
                    msgField.clear();
                }
            }
        });
        sendButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.add(sendButton, 1, 2, 1, 1);

        //Menu
        MenuBar menu = new MenuBar();

        Menu menuChat = new Menu("Chat");
        MenuItem mitemConnect = new MenuItem("Connect");
        mitemConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new ConnectFrame("127.0.0.1", "8610", "Alix");
            }
        });
        menuChat.getItems().addAll(mitemConnect);
        menu.getMenus().addAll(menuChat);
        grid.add(menu, 0, 0, 2, 1);

        //Scene
        Scene scene = new Scene(grid, 900, 500);
        primaryStage.setScene(scene);


        //NOTE: Last commands
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int loc_x = (screen.width - (int) scene.getWidth()) / 2;
        int loc_y = (screen.height - (int) scene.getHeight()) / 2;
        primaryStage.setX(loc_x);
        primaryStage.setY(loc_y);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (CryptoChatClient.connected) {
                    System.out.println("Disconnect");
                    CryptoChatClient.sockets.getStatusSocket().sendDisconnect();
                }
                System.exit(0);
            }
        });
        primaryStage.show();


    }

    public TextArea getChat() {
        return chat;
    }
}
