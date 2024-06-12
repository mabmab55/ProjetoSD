package com.mabmab.sistemasdistribuidos;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ControllerEmpresaInterface {

    public TextField razaoSocialEmpresa;
    public TextField emailEmpresa;
    public TextField cnpjEmpresa;
    public TextField senhaEmpresa;
    public TextField descricaoEmpresa;
    public TextField ramoEmpresa;

    private ChangeListener<String> emailChangeListener;

    public void initialize() {
        // Add the listener to the emailEmpresa text property
        emailChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Call getInitialData() when the text changes
                getInitialData();
            }
        };
        emailEmpresa.textProperty().addListener(emailChangeListener);
    }

    public void initData(String email) {
        emailEmpresa.setText(email);
        System.out.println("init data");
    }

    public void getInitialData() {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            String email = emailEmpresa.getText();
            JSONObject jsonObject = new JSONObject();
            System.out.println("trigger visualizar empresa");
            jsonObject.put("operacao", "visualizarEmpresa");
            jsonObject.put("email", email);
            jsonObject.put("token", ConnectionConfig.TOKEN);
            out.println(jsonObject.toString());
            System.out.println("Enviando " + jsonObject);

            // Read the response from the server
            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse.toString());
            String operacao = jsonResponse.getString("operacao");
            int status = jsonResponse.getInt("status");

            if (operacao.equals("visualizarEmpresa")) {
                if (status == 201) {
                    // Company data found, update labels
                    String razaoSocial = jsonResponse.getString("razaoSocial");
                    String cnpj = jsonResponse.getString("cnpj");
                    String senha = jsonResponse.getString("senha");
                    String descricao = jsonResponse.getString("descricao");
                    String ramo = jsonResponse.getString("ramo");
                    razaoSocialEmpresa.setText(razaoSocial);
                    cnpjEmpresa.setText(cnpj);
                    senhaEmpresa.setText(senha);
                    descricaoEmpresa.setText(descricao);
                    ramoEmpresa.setText(ramo);
                } else if (status == 404) {
                    // Company not found, handle error
                    String mensagem = jsonResponse.getString("mensagem");
                    System.out.println(mensagem); // Display error message
                } else {
                    // Handle other status codes if needed
                    System.out.println("Unexpected status code: " + status);
                }
            }

            emailEmpresa.textProperty().removeListener(emailChangeListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void attDadosEmpresa(ActionEvent actionEvent) {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Create JSON object with operation, email, and other fields
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "atualizarEmpresa");
            jsonObject.put("email", emailEmpresa.getText());
            jsonObject.put("razaoSocial", razaoSocialEmpresa.getText());
            jsonObject.put("cnpj", cnpjEmpresa.getText());
            jsonObject.put("senha", senhaEmpresa.getText());
            jsonObject.put("descricao", descricaoEmpresa.getText());
            jsonObject.put("ramo", ramoEmpresa.getText());
            jsonObject.put("token", ConnectionConfig.TOKEN);

            // Send JSON string to server
            out.println(jsonObject.toString());
            System.out.println(jsonObject);

            // Read the response from the server
            String response = in.readLine();
            System.out.println(response);

            JSONObject jsonResponse = new JSONObject(response);
            String operacao = jsonResponse.getString("operacao");
            int status = jsonResponse.getInt("status");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logoutEmpresa(ActionEvent actionEvent) throws IOException {
        PrintWriter out = SocketSingleton.getOutputWriter();
        BufferedReader in = SocketSingleton.getBufferedReader();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operacao", "logout");
        jsonObject.put("token", ConnectionConfig.TOKEN);

        out.println(jsonObject.toString());
        System.out.println(jsonObject);

        String response = in.readLine();
        System.out.println(response);
        SocketSingleton.closeSocket();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastroEmpresa.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cadastro");
        stage.show();
    }

    public void deleteEmpresa(ActionEvent actionEvent) {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Create JSON object with operation and email
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "apagarEmpresa");
            jsonObject.put("email", emailEmpresa.getText());

            // Send JSON string to server
            out.println(jsonObject.toString());
            System.out.println(jsonObject);

            String response = in.readLine();

            JSONObject jsonResponse = new JSONObject(response);
            String operacao = jsonResponse.getString("operacao");
            int status = jsonResponse.getInt("status");

            if (operacao.equals("apagarEmpresa")) {
                if (status == 201) {
                    // Company successfully deleted, update labels or clear them
                    razaoSocialEmpresa.setText("");
                    emailEmpresa.setText("");
                    cnpjEmpresa.setText("");
                    senhaEmpresa.setText("");
                    descricaoEmpresa.setText("");
                    ramoEmpresa.setText("");
                    System.out.println(jsonResponse.toString());
                } else if (status == 404) {
                    // Company not found, handle error
                    System.out.println("E-mail não encontrado");
                } else {
                    // Handle other status codes if needed
                    System.out.println("Unexpected status code: " + status);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
