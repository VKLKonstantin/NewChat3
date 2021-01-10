package client.controllers;

import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import client.models.Network;

public class AuthController {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;


    private Network network;
    private Client client;


    @FXML
    public void checkAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login.isBlank() || password.isBlank()) {//проверка, что поля ввода не пустые
            Client.showErrorMessage("Ошибка авторизации", "Ошибка ввода", "Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = network.sendAuthCommand(login, password);
        if (authErrorMessage != null) {
            Client.showErrorMessage("Ошибка авторизации", "Что-то не то", authErrorMessage);
        } else {
            client.openMainChatWindow();
        }

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setNetworkClient(Client client) {
        this.client = client;
    }
}
