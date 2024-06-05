package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerMenuCandidato {

    public String emailCandidato;

    public void initData(String email) {
        emailCandidato = email;
    }

    public void handleVisualizarDados(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("candidatointerface.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        ControllerCandidatointerface controller = fxmlLoader.getController();
        controller.initData(emailCandidato);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Candidato Interface");
        stage.show();
    }

    public void handleCadastrarCompetExp(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastrarCompetExp.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        stage.setTitle("Cadastrar Competência e Experiência");
        stage.show();
    }

    public void handleVisualizarCompetExp(ActionEvent actionEvent) {
    }

    public void handleVisualizarVagas(ActionEvent actionEvent) {
    }
}
