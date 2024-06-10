package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ControllerCadastrarVaga {

    @FXML
    private TextField nomeVagaField;

    @FXML
    private TextField faixaSalarialField;

    @FXML
    private TextArea descricaoArea;

    @FXML
    private TextField estadoField;

    @FXML
    private TextArea competenciasArea;

    @FXML
    private void cadastrarVaga() {
        try {
            // Capture os dados dos campos
            String nome = nomeVagaField.getText();
            String faixaSalarial = faixaSalarialField.getText();
            String descricao = descricaoArea.getText();
            String estado = estadoField.getText();
            String competenciasText = competenciasArea.getText();

            // Divida as competências em uma lista
            String[] competenciasArray = competenciasText.split(",");

            // Construa o objeto JSON
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "cadastrarVaga");
            jsonObject.put("nome", nome);
            jsonObject.put("email", ConnectionConfig.EMAIL);
            jsonObject.put("faixaSalarial", Double.parseDouble(faixaSalarial));
            jsonObject.put("descricao", descricao);
            jsonObject.put("estado", estado);

            JSONArray competenciasJsonArray = new JSONArray();
            for (String competencia : competenciasArray) {
                competenciasJsonArray.put(competencia.trim());
            }
            jsonObject.put("competencias", competenciasJsonArray);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            // Envie o objeto JSON para o servidor
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();
            out.println(jsonObject.toString());
            System.out.println("Enviando: " + jsonObject);

            // Leia a resposta do servidor
            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            // Verifique o status da resposta
            int status = jsonResponse.getInt("status");
            String mensagem = jsonResponse.getString("mensagem");

            // Exiba uma mensagem ao usuário
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (status == 201) {
                alert.setTitle("Sucesso");
                alert.setHeaderText("Vaga cadastrada com sucesso");
                alert.setContentText(mensagem);
            } else {
                alert.setTitle("Erro");
                alert.setHeaderText("Erro ao cadastrar vaga");
                alert.setContentText(mensagem);
            }
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro de comunicação", "Não foi possível se comunicar com o servidor.");
        } catch (NumberFormatException e) {
            showAlert("Erro de formato", "A faixa salarial deve ser um número.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void voltar(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuEmpresa.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Empresa Menu");
        stage.show();
    }
}
