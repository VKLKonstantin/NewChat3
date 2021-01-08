package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Network;


import java.io.IOException;

public class MainWindowController {


    @FXML
    private ListView listView;

    @FXML
    private Button button;

    @FXML
    private TextField textField;


    @FXML
    private TextArea chatHistory;

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {
      //  listView.setItems(FXCollections.observableArrayList(Client.USERS_TEST_DATA));
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainWindowController.this.sendMessage();
            }
        });
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MainWindowController.this.sendMessage();
            }
        });
    }


    public void sendMessage() {
        String message = textField.getText();
       // appendMessage(message);
        textField.clear();
        try {
            network.getOut().writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
          //  Client.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }
    }

    public void appendMessage(String message) {
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
    }
}
