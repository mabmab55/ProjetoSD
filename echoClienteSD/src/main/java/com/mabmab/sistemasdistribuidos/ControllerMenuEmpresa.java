package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerMenuEmpresa {
    public void handleVisualizarDados(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("empresainterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        ControllerEmpresaInterface controller = fxmlLoader.getController();
        controller.initData(ConnectionConfig.EMAIL);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Empresa Interface");
        stage.show();
    }

    public void handleCadastrarVagas(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cadastrarVaga.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        stage.setTitle("Cadastrar vaga");
        stage.show();
    }

    public void handleListarVagas(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("listarVagas.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        stage.setTitle("Listar vagas");
        stage.show();
    }

}
