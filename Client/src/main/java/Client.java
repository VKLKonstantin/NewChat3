import controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import models.Network;

import java.io.IOException;
import java.util.List;

public class Client extends Application {

  //  public static final List<String> USERS_TEST_DATA = List.of("Boris Nikolaevich", "Martin Nekotov", "Gandalf the White");
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("main_window.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Messenger");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        Network network = new Network();
        if (!network.connect()) {
            System.out.println("Ошибка подключения");
        }

        MainWindowController mainWindowController = loader.getController();
        mainWindowController.setNetwork(network);
        network.waitMessage(mainWindowController);

        primaryStage.setOnCloseRequest(windowEvent -> network.close());
    }

  /*  public static void showErrorMessage(String title, String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }*/
    public static void main(String[] args) {
        launch(args);
    }
}
