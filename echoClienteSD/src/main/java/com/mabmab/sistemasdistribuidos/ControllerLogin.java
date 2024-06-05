package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControllerLogin {
    public TextField emailLogin;
    public TextField senhaLogin;


    @FXML
    public void initialize() {

    }


    public void onSendClick(ActionEvent actionEvent) throws IOException {
        PrintWriter out = SocketSingleton.getOutputWriter();
        BufferedReader in = SocketSingleton.getBufferedReader();

        String email = emailLogin.getText();
        String senha = senhaLogin.getText();

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "loginCandidato");
        jsonObject.put("email", email);
        jsonObject.put("senha", senha);

        // Send JSON string to server
        out.println(jsonObject.toString());
        System.out.println("Enviando : "+jsonObject);

        // Clear the text fields after sending
        senhaLogin.clear();
        emailLogin.clear();

        try {
            // Read the response from the server
            String response = in.readLine();

            // Parse the response JSON
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getInt("status") == 200) {
                ConnectionConfig.TOKEN = responseJson.getString("token");
                ConnectionConfig.EMAIL = email;
                System.out.println(ConnectionConfig.TOKEN + " token");
                System.out.println(ConnectionConfig.EMAIL + " email");
            }
            // Check the login status
            int status = responseJson.getInt("status");
            if (status == 200) {
                System.out.println(responseJson);
                // Successful login, redirect to menuCandidato
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuCandidato.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                ControllerMenuCandidato controller = fxmlLoader.getController();
                controller.initData(email);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Candidato Interface");
                stage.show();
            } else {
                System.out.println(responseJson.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void irParaCadastro(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastro.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 360);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cadastro");
        stage.show();
    }
}