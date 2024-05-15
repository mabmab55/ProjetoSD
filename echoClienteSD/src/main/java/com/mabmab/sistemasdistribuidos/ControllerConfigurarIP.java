package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerConfigurarIP {

    public TextField insertedIP;

    public void clickDefinirIP(ActionEvent actionEvent) throws IOException {
        String IP = insertedIP.getText();

        ConnectionConfig.SERVER_IP = IP;
        System.out.println(ConnectionConfig.SERVER_IP);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastro.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cadastro");
        stage.show();
    }
}
