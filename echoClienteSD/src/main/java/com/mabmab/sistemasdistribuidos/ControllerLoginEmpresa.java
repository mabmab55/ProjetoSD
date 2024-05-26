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
import java.io.PrintWriter;

public class ControllerLoginEmpresa {
    public TextField emailLogin;
    public TextField senhaLogin;

    @FXML
    public void initialize() {
        // Initialize if needed
    }

    public void onSendClick(ActionEvent actionEvent) throws IOException {
        PrintWriter out = SocketSingleton.getOutputWriter();
        BufferedReader in = SocketSingleton.getBufferedReader();

        String email = emailLogin.getText();
        String senha = senhaLogin.getText();

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "loginEmpresa");
        jsonObject.put("email", email);
        jsonObject.put("senha", senha);

        // Send JSON string to server
        out.println(jsonObject.toString());
        System.out.println("Enviando: " + jsonObject);

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
                System.out.println(ConnectionConfig.TOKEN + " token");
            }
            // Check the login status
            int status = responseJson.getInt("status");
            if (status == 200) {
                System.out.println(responseJson.toString());
                // Successful login, redirect to EmpresaInterface
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("empresainterface.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                ControllerEmpresaInterface controller = fxmlLoader.getController();
                controller.initData(email);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Empresa Interface");
                stage.show();
            } else {
                System.out.println(responseJson.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void irParaCadastro(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastroEmpresa.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 360);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cadastro Empresa");
        stage.show();
    }
}
