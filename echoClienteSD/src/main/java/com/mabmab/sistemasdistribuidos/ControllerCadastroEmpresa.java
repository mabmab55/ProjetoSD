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

public class ControllerCadastroEmpresa {
    public TextField razaoSocial;
    public TextField emailE;
    public TextField cnpjE;
    public TextField senhaE;
    public TextField descricaoE;
    public TextField ramoE;

    @FXML
    public void initialize() {
        // Initialization code if needed
    }

    @FXML
    public void onSendClick() throws IOException {
        PrintWriter out = SocketSingleton.getOutputWriter();
        BufferedReader in = SocketSingleton.getBufferedReader();

        String razaoSocialText = razaoSocial.getText();
        String emailText = emailE.getText();
        String cnpjText = cnpjE.getText();
        String senhaText = senhaE.getText();
        String descricaoText = descricaoE.getText();
        String ramoText = ramoE.getText();

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "cadastrarEmpresa");
        jsonObject.put("razaoSocial", razaoSocialText);
        jsonObject.put("email", emailText);
        jsonObject.put("cnpj", cnpjText);
        jsonObject.put("senha", senhaText);
        jsonObject.put("descricao", descricaoText);
        jsonObject.put("ramo", ramoText);

        // Send JSON string to server
        out.println(jsonObject.toString());
        System.out.println("Enviando: " + jsonObject);

        // Clear the text fields after sending
        razaoSocial.clear();
        emailE.clear();
        cnpjE.clear();
        senhaE.clear();
        descricaoE.clear();
        ramoE.clear();

        try {
            String response = in.readLine();
            System.out.println(response);
            JSONObject responseJson = new JSONObject(response);
            ConnectionConfig.TOKEN = responseJson.getString("token");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void irParaLoginE(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginEmpresa.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 360);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
