package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ControllerCadastrarCompetExp {
    public TextField experienciaTextField;
    public TextField competenciaTextField;
    public TextField emailTextField;

    public void initialize() {
        emailTextField.setText(ConnectionConfig.EMAIL);
    }

    public void handleSalvarCompetExp(ActionEvent actionEvent) throws IOException {
        // Extrair os dados dos campos de texto
        String competencia = competenciaTextField.getText();
        String email = emailTextField.getText();
        int experiencia = Integer.parseInt(experienciaTextField.getText());

        // Construir o objeto JSON
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "cadastrarCompetenciaExperiencia");
        jsonObject.put("email", email);

        JSONArray competenciaExperienciaArray = new JSONArray();

        JSONObject competenciaExperiencia1 = new JSONObject();
        competenciaExperiencia1.put("competencia", competencia);
        competenciaExperiencia1.put("experiencia", experiencia);

        // Adicionar ao array
        competenciaExperienciaArray.put(competenciaExperiencia1);

        jsonObject.put("competenciaExperiencia", competenciaExperienciaArray);

        jsonObject.put("token", "UUID");

        // Enviar JSON para o servidor
        PrintWriter out = SocketSingleton.getOutputWriter();
        out.println(jsonObject.toString());
        System.out.println("Enviando " + jsonObject);

        BufferedReader in = SocketSingleton.getBufferedReader();
        System.out.println("Recebido" + in.readLine());
    }

    public void handleRetornar(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuCandidato.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        stage.setTitle("MENU");
        stage.show();
    }
}
