package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ControllerVisualizarVaga {

    @FXML
    private TextField faixaSalarialField;

    @FXML
    private TextArea descricaoArea;

    @FXML
    private TextField estadoField;

    @FXML
    private TextArea competenciasArea;

    private int idVaga;
    private String nomeVaga;


    public void setVaga(int idVaga, String nomeVaga) {
        this.idVaga = idVaga;
        this.nomeVaga = nomeVaga;
        visualizarVaga();
    }

    @FXML
    private void visualizarVaga() {
        try {
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Construir o objeto JSON para a solicitação
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "visualizarVaga");
            jsonObject.put("idVaga", idVaga);
            jsonObject.put("email", ConnectionConfig.EMAIL);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            out.println(jsonObject.toString());
            System.out.println("Enviando: " + jsonObject);

            // Ler a resposta do servidor
            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println("recebi: " + jsonResponse);

            if (jsonResponse.getInt("status") == 201) {
                faixaSalarialField.setText(String.valueOf(jsonResponse.getDouble("faixaSalarial")));
                descricaoArea.setText(jsonResponse.getString("descricao"));
                estadoField.setText(jsonResponse.getString("estado"));

                JSONArray competenciasArray = jsonResponse.getJSONArray("competencias");
                StringBuilder competencias = new StringBuilder();
                for (int i = 0; i < competenciasArray.length(); i++) {
                    competencias.append(competenciasArray.getString(i)).append(",");
                }
                competenciasArea.setText(competencias.toString());

            } else {
                showErrorAlert("Erro ao visualizar vaga", jsonResponse.getString("mensagem"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erro de comunicação", "Não foi possível se comunicar com o servidor.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void voltar(ActionEvent actionEvent) {
        Stage stage = (Stage) faixaSalarialField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void atualizarVaga() {
        try {
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Construir o objeto JSON para a solicitação
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "atualizarVaga");
            jsonObject.put("idVaga", idVaga);
            jsonObject.put("nome", nomeVaga);
            jsonObject.put("faixaSalarial", Double.parseDouble(faixaSalarialField.getText()));
            jsonObject.put("descricao", descricaoArea.getText());
            jsonObject.put("estado", estadoField.getText());

            String[] competencias = competenciasArea.getText().split(",");
            JSONArray competenciasArray = new JSONArray();
            for (String competencia : competencias) {
                competenciasArray.put(competencia);
            }
            jsonObject.put("competencias", competenciasArray);

            jsonObject.put("email", ConnectionConfig.EMAIL);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            out.println(jsonObject.toString());
            System.out.println("Enviando: " + jsonObject);

            // Ler a resposta do servidor
            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println("recebi: " + jsonResponse);

            if (jsonResponse.getInt("status") == 201) {
                // Vaga atualizada com sucesso
                // Você pode exibir uma mensagem de sucesso aqui se necessário
            } else {
                showErrorAlert("Erro ao atualizar vaga", jsonResponse.getString("mensagem"));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            showErrorAlert("Erro de comunicação", "Não foi possível se comunicar com o servidor.");
        }
    }

}
