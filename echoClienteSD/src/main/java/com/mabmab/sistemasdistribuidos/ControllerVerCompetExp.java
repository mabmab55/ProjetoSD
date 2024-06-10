package com.mabmab.sistemasdistribuidos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerVerCompetExp {

    @FXML
    private TableView<CompetenciaExperiencia> tableView;
    @FXML
    private TableColumn<CompetenciaExperiencia, String> colCompetencia;
    @FXML
    private TableColumn<CompetenciaExperiencia, Integer> colExperiencia;
    @FXML
    private TableColumn<CompetenciaExperiencia, Void> colAcoes;

    public void initialize() {
        colCompetencia.setCellValueFactory(new PropertyValueFactory<>("competencia"));
        colExperiencia.setCellValueFactory(new PropertyValueFactory<>("experiencia"));

        colCompetencia.setPrefWidth(200);
        colExperiencia.setPrefWidth(200);
        colAcoes.setPrefWidth(250);

        addButtonToTable();

        getInitialData();
    }

    private void getInitialData() {
        try {
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            String email = ConnectionConfig.EMAIL;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "visualizarCompetenciaExperiencia");
            jsonObject.put("email", email);
            jsonObject.put("token", ConnectionConfig.TOKEN);
            out.println(jsonObject.toString());
            System.out.println("Enviando " + jsonObject);

            // Read response
            String response = in.readLine();

            JSONObject jsonResponse = new JSONObject(response);
            System.out.println(jsonResponse.toString());

            JSONArray competencias = jsonResponse.getJSONArray("competenciaExperiencia");
            List<CompetenciaExperiencia> dataList = new ArrayList<>();
            for (int i = 0; i < competencias.length(); i++) {
                JSONObject compExp = competencias.getJSONObject(i);
                String competencia = compExp.getString("competencia");
                int experiencia = compExp.getInt("experiencia");
                dataList.add(new CompetenciaExperiencia(competencia, experiencia));
            }

            tableView.getItems().setAll(dataList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<CompetenciaExperiencia, Void>, TableCell<CompetenciaExperiencia, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<CompetenciaExperiencia, Void> call(final TableColumn<CompetenciaExperiencia, Void> param) {
                return new TableCell<>() {

                    private final Button btnEdit = new Button("Editar");
                    private final Button btnDelete = new Button("Deletar");

                    {
                        btnEdit.setOnAction(event -> {
                            CompetenciaExperiencia data = getTableView().getItems().get(getIndex());
                            handleEditAction(data);
                        });

                        btnDelete.setOnAction(event -> {
                            CompetenciaExperiencia data = getTableView().getItems().get(getIndex());
                            handleDeleteAction(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hBox = new HBox(btnEdit, btnDelete);
                            hBox.setSpacing(10);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };

        colAcoes.setCellFactory(cellFactory);
    }

    private void handleEditAction(CompetenciaExperiencia data) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(data.getExperiencia()));
        dialog.setTitle("Editar Experiência");
        dialog.setHeaderText("Editar Experiência para " + data.getCompetencia());
        dialog.setContentText("Nova Experiência:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int novaExperiencia = Integer.parseInt(result.get());

                // Construa o JSON de solicitação
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("operacao", "atualizarCompetenciaExperiencia");
                jsonObject.put("email", ConnectionConfig.EMAIL);

                JSONArray competenciaExperienciaArray = new JSONArray();
                JSONObject compExpJson = new JSONObject();
                compExpJson.put("competencia", data.getCompetencia());
                compExpJson.put("experiencia", novaExperiencia);
                competenciaExperienciaArray.put(compExpJson);

                jsonObject.put("competenciaExperiencia", competenciaExperienciaArray);
                jsonObject.put("token", ConnectionConfig.TOKEN);

                // Envie a solicitação para o servidor
                PrintWriter out = SocketSingleton.getOutputWriter();
                BufferedReader in = SocketSingleton.getBufferedReader();
                out.println(jsonObject.toString());
                System.out.println("Enviando " + jsonObject);

                // Leia a resposta do servidor
                String response = in.readLine();
                JSONObject jsonResponse = new JSONObject(response);
                System.out.println("Resposta: " + jsonResponse);

                // Verifique o status da resposta
                if (jsonResponse.getInt("status") == 201) {
                    // Atualize a tabela localmente com a nova experiência
                    data.setExperiencia(novaExperiencia);
                    tableView.refresh(); // Atualiza a visualização da tabela
                } else {
                    // Trate o caso de erro aqui
                    System.out.println("Erro ao atualizar a experiência: " + jsonResponse.getString("mensagem"));
                }
            } catch (NumberFormatException e) {
                // Se o usuário não inserir um número válido
                System.out.println("Por favor, insira um número válido para a experiência.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void handleDeleteAction(CompetenciaExperiencia data) {
        try {
            // Construa o JSON de solicitação
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "apagarCompetenciaExperiencia");
            jsonObject.put("email", ConnectionConfig.EMAIL);

            JSONArray competenciaExperienciaArray = new JSONArray();
            JSONObject compExp = new JSONObject();
            compExp.put("competencia", data.getCompetencia());
            compExp.put("experiencia", data.getExperiencia());
            competenciaExperienciaArray.put(compExp);

            jsonObject.put("competenciaExperiencia", competenciaExperienciaArray);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            // Envie a solicitação para o servidor
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();
            out.println(jsonObject.toString());
            System.out.println("Enviando " + jsonObject);

            // Leia a resposta do servidor
            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println("Resposta: " + jsonResponse);

            // Verifique o status da resposta
            if (jsonResponse.getInt("status") == 201) {
                // Atualize a tabela removendo o item deletado
                tableView.getItems().remove(data);
            } else {
                // Trate o caso de erro aqui
                System.out.println("Erro ao deletar a competência/experiência: " + jsonResponse.getString("mensagem"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void voltar(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuCandidato.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Candidato Interface");
        stage.show();
    }
}
