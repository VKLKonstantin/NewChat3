package client;

import client.controllers.AuthController;
import client.controllers.MainWindowController;
import client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Client extends Application {


    public static final List<String> USERS_TEST_DATA = List.of("Костя", "Надя", "Гендальф_Серый");
    private Stage primaryStage;
    private Stage authStage;
    private Network network;
    private MainWindowController mainWindowController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        network = new Network();
        if (!network.connect()) {
            showErrorMessage("Проблемы с соединением", "", "Ошибка подключения к серверу");
            return;
        }

        openAuthWindow();
        createMainChatWindow();
    }

    private void openAuthWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/auth-window.fxml"));
        Parent root = loader.load();
        authStage = new Stage();

        authStage.setTitle("Авторизация");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        Scene scene = new Scene(root);
        authStage.setScene(scene);
        authStage.show();

        AuthController authController = loader.getController();
        authController.setNetwork(network);
        authController.setNetworkClient(this);


    }

    public void createMainChatWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("/main-window.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Чат");
        primaryStage.setScene(new Scene(root, 600, 400));

        mainWindowController = loader.getController();
        mainWindowController.setNetwork(network);

        primaryStage.setOnCloseRequest(windowEvent -> network.close());
    }

    public static void showErrorMessage(String title, String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openMainChatWindow() {
        authStage.close();
        primaryStage.show();

        primaryStage.setTitle(network.getUsername());
        primaryStage.setAlwaysOnTop(true);
        mainWindowController.setLabel(network.getUsername());
        network.waitMessage(mainWindowController);
    }

}
