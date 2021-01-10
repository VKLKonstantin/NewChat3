package client.controllers;

import client.Client;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class MainWindowController {


    @FXML
    private ListView listView;

    @FXML
    private Button button;

    @FXML
    private TextField textField;


    @FXML
    private TextArea chatHistory;

    @FXML
    private Label usernameTitle;

    private Network network;

    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {
        listView.setItems(FXCollections.observableArrayList(Client.USERS_TEST_DATA));
        button.setOnAction(event -> MainWindowController.this.sendMessage());
        textField.setOnAction(event -> MainWindowController.this.sendMessage());
    }

    private void sendMessage() {
        String message = textField.getText();
        appendMessage("Я: " + message);
        textField.clear();

        try {
            network.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
            Client.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }

    }

    public void appendMessage(String message) {
        String timestamp = DateFormat.getInstance().format(new Date());
        chatHistory.appendText(timestamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
    }

    public void setUsernameTitle(String username) {

    }
}
