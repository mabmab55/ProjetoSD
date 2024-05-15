package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.stage.Stage;
import org.json.JSONObject;

public class ControllerCadastro {

    public TextField nomeCadastro;
    public TextField emailCadastro;
    public TextField senhaCadastro;


    @FXML
    public void initialize() {

    }

    @FXML
    public void onSendClick() throws IOException {
        PrintWriter out = SocketSingleton.getOutputWriter();
        BufferedReader in = SocketSingleton.getBufferedReader();

        String nome = nomeCadastro.getText();
        String email = emailCadastro.getText();
        String senha = senhaCadastro.getText();

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "cadastrarCandidato");
        jsonObject.put("nome", nome);
        jsonObject.put("email", email);
        jsonObject.put("senha", senha);

        // Send JSON string to server
        out.println(jsonObject.toString());
        System.out.println("Enviando : "+jsonObject);

        // Clear the text fields after sending
        nomeCadastro.clear();
        emailCadastro.clear();
        senhaCadastro.clear();

        try {
            String response = in.readLine();
            System.out.println(response);
            JSONObject responseJson = new JSONObject(response);
            ConnectionConfig.TOKEN = responseJson.getString("token");
            //System.out.println(ConnectionConfig.TOKEN + " token");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void irParaLogin(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 360);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    public void clickConfigurarIP(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("configurarIP.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 360);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("IP");
        stage.show();
    }
}
