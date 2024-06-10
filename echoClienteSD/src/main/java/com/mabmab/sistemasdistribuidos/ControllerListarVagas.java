package com.mabmab.sistemasdistribuidos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ControllerListarVagas {

    @FXML
    private TableView<Vaga> vagasTableView;

    @FXML
    private TableColumn<Vaga, Integer> idVagaColumn;

    @FXML
    private TableColumn<Vaga, String> nomeVagaColumn;

    @FXML
    private TableColumn<Vaga, Void> actionsColumn;

    private ObservableList<Vaga> vagasList;

    @FXML
    public void initialize() {
        idVagaColumn.setCellValueFactory(new PropertyValueFactory<>("idVaga"));
        nomeVagaColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));

        // Configura a coluna de ações para ter um botão "Visualizar"
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Vaga, Void> call(final TableColumn<Vaga, Void> param) {
                final TableCell<Vaga, Void> cell = new TableCell<>() {
                    private final Button visualizarBtn = new Button("Visualizar");
                    private final Button apagarBtn = new Button("Apagar");

                    {
                        visualizarBtn.setOnAction(event -> {
                            Vaga vaga = getTableView().getItems().get(getIndex());
                            handleVisualizarVaga(vaga.getIdVaga(), vaga.getNome());
                        });

                        apagarBtn.setOnAction(event -> {
                            Vaga vaga = getTableView().getItems().get(getIndex());
                            handleApagarVaga(vaga.getIdVaga());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttonsBox = new HBox(visualizarBtn, apagarBtn);
                            setGraphic(buttonsBox);
                        }
                    }
                };
                return cell;
            }
        });

        vagasList = FXCollections.observableArrayList();
        vagasTableView.setItems(vagasList);

        listarVagas();
    }

    private void handleApagarVaga(int idVaga) {
        try {
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "apagarVaga");
            jsonObject.put("idVaga", idVaga);
            jsonObject.put("email", ConnectionConfig.EMAIL);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            out.println(jsonObject.toString());
            System.out.println("Enviando :" + jsonObject);

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println("Recebi :" + jsonResponse);

            if (jsonResponse.getInt("status") == 201) {
                // Vaga apagada com sucesso, atualize a lista de vagas
                listarVagas();
            } else {
                showErrorAlert("Erro ao apagar vaga", jsonResponse.getString("mensagem"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erro de comunicação", "Não foi possível se comunicar com o servidor.");
        }
    }

    private void listarVagas() {
        try {
            PrintWriter out = SocketSingleton.getOutputWriter();
            BufferedReader in = SocketSingleton.getBufferedReader();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operacao", "listarVagas");
            jsonObject.put("email", ConnectionConfig.EMAIL);
            jsonObject.put("token", ConnectionConfig.TOKEN);

            out.println(jsonObject.toString());
            System.out.println("Enviando :" + jsonObject);

            String response = in.readLine();
            JSONObject jsonResponse = new JSONObject(response);
            System.out.println("Recebi :" + jsonResponse);

            if (jsonResponse.getInt("status") == 201) {
                JSONArray vagasArray = jsonResponse.getJSONArray("vagas");

                List<Vaga> vagas = new ArrayList<>();
                for (int i = 0; i < vagasArray.length(); i++) {
                    JSONObject vagaJson = vagasArray.getJSONObject(i);
                    int idVaga = vagaJson.getInt("idVaga");
                    String nome = vagaJson.getString("nome");
                    Vaga vaga = new Vaga(idVaga, nome);
                    vagas.add(vaga);
                }

                vagasList.setAll(vagas);
            } else {
                showErrorAlert("Erro ao listar vagas", jsonResponse.getString("mensagem"));
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

    private void handleVisualizarVaga(int idVaga, String nomeVaga) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("verVaga.fxml"));
            Parent root = loader.load();

            ControllerVisualizarVaga controller = loader.getController();
            controller.setVaga(idVaga, nomeVaga);

            Stage stage = new Stage();
            stage.setTitle("Visualizar Vaga");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void voltar(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menuEmpresa.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Empresa Menu");
        stage.show();
    }

    public static class Vaga {
        private final Integer idVaga;
        private final String nome;

        public Vaga(Integer idVaga, String nome) {
            this.idVaga = idVaga;
            this.nome = nome;
        }

        public Integer getIdVaga() {
            return idVaga;
        }

        public String getNome() {
            return nome;
        }
    }
}
