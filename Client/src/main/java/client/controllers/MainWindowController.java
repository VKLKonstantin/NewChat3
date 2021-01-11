package client.controllers;

import client.Client;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String selectedRecipient;

    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    public void initialize() {

        button.setOnAction(event -> {
            try {
                MainWindowController.this.sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        textField.setOnAction(event -> {
            try {
                MainWindowController.this.sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendMessage() throws IOException {
        String message = textField.getText();
        appendMessage("Я: " + message);
        textField.clear();

        if(message.isBlank()) {
            return;
        }

        appendMessage("Я: " + message);
        textField.clear();

        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(message, selectedRecipient);
            }
            else {
                network.sendMessage(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Client.showErrorMessage("Ошибка подключения", "Ошибка при отправке сообщения", e.getMessage());
        }

    }

    public void appendMessage(String message) throws IOException {
        String timestamp = DateFormat.getInstance().format(new Date());
        chatHistory.appendText(timestamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
        historyOfChat();
    }

    public void setUsernameTitle(String username) {

    }
    public void updateUsers(List<String> users) {
        listView.setItems(FXCollections.observableArrayList(users));
    }

    private void historyOfChat() throws IOException {

        try {
            File file = new File("Client/src/main/java/client/historyOfChat.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter fileWriter = new PrintWriter(new FileWriter(file, true));

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(chatHistory.getText());
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void load100HistoryForClient() throws IOException {
        int countStringHistory = 100;

        File history = new File("ChatClient/src/client/historyOfChat.txt");
        if (!history.exists()) {
            history.createNewFile();
        }
        List<String> historyList = new ArrayList<>();
        FileInputStream in = new FileInputStream(history);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String temp;
        while ((temp = bufferedReader.readLine()) != null) {
            if(temp==null){
                return;
            }
            historyList.add(temp);
        }

        if (historyList.size() > countStringHistory) {
            for (int i = historyList.size() - countStringHistory; i <= (historyList.size() - 1); i++) {
                chatHistory.appendText(historyList.get(i) + "\n");
            }
        } else {
            for (int i = 0; i < countStringHistory; i++) {
                chatHistory.appendText(historyList.get(i) + "\n");
            }
        }
    }
}
