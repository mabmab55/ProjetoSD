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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ControllerCandidatointerface {

    public TextField nomeCandidato;
    public TextField emailCandidato;
    public TextField senhaCandidato;
    private Socket echoSocket;
    private PrintWriter out;
    private BufferedReader in;

    private ChangeListener<String> emailChangeListener;

    public void initialize() {
        // Add the listener to the emailCandidato text property
        emailChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Call getInitialData() when the text changes
                getInitialData();
            }
        };
        emailCandidato.textProperty().addListener(emailChangeListener);
    }

    public void initData(String email) {
        emailCandidato.setText(email);
    }

    public void getInitialData() {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            String email = emailCandidato.getText();
            //System.out.println(email);
            JSONObject jsonObject = new JSONObject();
            System.out.println("trigger visualizar candidato");
            jsonObject.put("operacao", "visualizarCandidato");
            jsonObject.put("email", email);
            out.println(jsonObject.toString());
            System.out.println("Enviando " + jsonObject);

            // Read the response from the server
            String response = in.readLine();
            // Process the response as needed
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse.toString());
            String operacao = jsonResponse.getString("operacao");
            int status = jsonResponse.getInt("status");

            if (operacao.equals("visualizarCandidato")) {
                if (status == 201) {
                    // Candidate data found, update labels
                    String nome = jsonResponse.getString("nome");
                    String senha = jsonResponse.getString("senha");
                    nomeCandidato.setText(nome);
                    senhaCandidato.setText(senha);
                } else if (status == 404) {
                    // Candidate not found, handle error
                    String mensagem = jsonResponse.getString("mensagem");
                    System.out.println(mensagem); // Display error message
                } else {
                    // Handle other status codes if needed
                    System.out.println("Un" +
                            "expected status code: " + status);
                }
            }

            emailCandidato.textProperty().removeListener(emailChangeListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void attDadosCandidato(ActionEvent actionEvent) {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Create JSON object with operation, email, nome, and senha
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "atualizarCandidato");
            jsonObject.put("email", emailCandidato.getText());
            jsonObject.put("nome", nomeCandidato.getText());
            jsonObject.put("senha", senhaCandidato.getText());

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


    public void logoutCandidato(ActionEvent actionEvent) throws IOException {
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastro.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Cadastro");
        stage.show();
    }

    public void deleteCandidato(ActionEvent actionEvent) {
        try {
            // Connect to the Echo server
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            // Create JSON object with operation and email
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "apagarCandidato");
            jsonObject.put("email", emailCandidato.getText());

            // Send JSON string to server
            out.println(jsonObject.toString());
            System.out.println(jsonObject);

            String response = in.readLine();

            JSONObject jsonResponse = new JSONObject(response);
            String operacao = jsonResponse.getString("operacao");
            int status = jsonResponse.getInt("status");

            if (operacao.equals("apagarCandidato")) {
                if (status == 201) {
                    // Candidate successfully deleted, update labels or clear them
                    nomeCandidato.setText("");
                    emailCandidato.setText("");
                    senhaCandidato.setText("");
                    System.out.println(jsonResponse.toString());
                } else if (status == 404) {
                    // Candidate not found, handle error
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
