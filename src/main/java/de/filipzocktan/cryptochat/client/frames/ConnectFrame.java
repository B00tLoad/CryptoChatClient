package de.filipzocktan.cryptochat.client.frames;


import de.filipzocktan.cryptochat.client.CryptoChatClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.awt.*;
import java.util.Base64;

public class ConnectFrame {

    private String host;
    private String port;
    private String uname;

    public ConnectFrame() {
        this("", "8610", "");
    }

    public ConnectFrame(String host, String port, String uname) {
        this.host = host;
        this.port = port;
        this.uname = uname;
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        start(stage);
    }


    public void start(Stage primaryStage) {

        //edit Stage
        primaryStage.setTitle("Connect | CryptoChat");
        primaryStage.setResizable(false);

        //GridPane-Layout
        GridPane grid = new GridPane();
//        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col3.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        //Scene
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("de/filipzocktan/cryptochat/client/frames/style/connect.css");

        //Elemente
        TextField hosttf = new TextField(host);
        hosttf.setPromptText("Host-Name/IP-Address");
        Tooltip hosttooltip = new Tooltip();
        hosttooltip.setText(hosttf.getPromptText());
        hosttf.setTooltip(hosttooltip);
        hosttf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hosttf.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        hosttf.setText(host);
        grid.add(hosttf, 0, 0, 3, 1);


        TextField porttf = new TextField(port);
        porttf.setPromptText("Port (Default is 8610)");
        Tooltip porttooltip = new Tooltip();
        porttooltip.setText(porttf.getPromptText());
        porttf.setTooltip(porttooltip);
        porttf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        porttf.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        porttf.setText(port);
        grid.add(porttf, 3, 0, 1, 1);


        TextField unametf = new TextField(uname);
        unametf.setPromptText("Username");
        Tooltip unametooltip = new Tooltip();
        unametooltip.setText(unametf.getPromptText());
        unametf.setTooltip(unametooltip);
        unametf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        unametf.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        unametf.setText(uname);
        grid.add(unametf, 0, 1, 2, 1);

        PasswordField passwordtf = new PasswordField();
        passwordtf.setPromptText("Password");
        Tooltip passworttooltip = new Tooltip();
        passworttooltip.setText(passwordtf.getPromptText());
        passwordtf.setTooltip(passworttooltip);
        passwordtf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        passwordtf.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        grid.add(passwordtf, 2, 1, 2, 1);

        Label infoLbl = new Label();
        infoLbl.getStyleClass().add("errorlabel");
        grid.add(infoLbl, 0, 2, 2, 1);

        Button connectButton = new Button("Connect");
        connectButton.setDefaultButton(true);
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                infoLbl.setText("");
                boolean exe = true;
                int port_tmp = 0;
                String pw_tmp = new String(Base64.getEncoder().encode(passwordtf.getText().getBytes()));

                //check host and stop login if unvalid
                UrlValidator urlValidator = new UrlValidator();
                InetAddressValidator ipValidator = new InetAddressValidator();

                String host_tmp = hosttf.getText();
                if (urlValidator.isValid("http://" + host_tmp)) {
                } else if (!ipValidator.isValidInet4Address(host_tmp)) {
                    exe = false;
                    infoLbl.setText(infoLbl.getText() + "Host/IP-Address is not valid.\n");
                }


                //check Username and stop login if invalid
                String uname_tmp = unametf.getText();
                if (!org.apache.commons.lang3.StringUtils.isAlphanumeric(uname_tmp)) {
                    exe = false;
                    infoLbl.setText(infoLbl.getText() + "Username is not valid.\n");
                }

                //Parse port and stop login if invalid
                try {
                    port_tmp = Integer.parseInt(porttf.getText());
                } catch (NumberFormatException e) {
                    exe = false;
                    infoLbl.setText(infoLbl.getText() + "Port is not valid.\n");
                }
                if (exe) {
                    CryptoChatClient.login(host_tmp, port_tmp, uname_tmp, pw_tmp);
                    CryptoChatClient.startServices();
                    primaryStage.close();
                }
            }
        });
        connectButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        connectButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        grid.add(connectButton, 3, 2, 1, 1);

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cancelButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        grid.add(cancelButton, 2, 2, 1, 1);

        //NOTE: Last commands
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int loc_x = (screen.width - (int) scene.getWidth()) / 2;
        int loc_y = (screen.height - (int) scene.getHeight()) / 2;
        primaryStage.setX(loc_x);
        primaryStage.setY(loc_y);
        primaryStage.show();

    }
}
