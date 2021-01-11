package client.models;

import client.Client;
import client.controllers.MainWindowController;
import clientservice.Command;
import clientservice.commands.*;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class Network {//отвечает за связь с сервером
    //подключение с сервером
    //ожидание сообщений
    //отправка сообщений

   /* private static final String AUTH_CMD_PREFIX = "/auth";
    private static final String AUTHOK_CMD_PREFIX = "/authok";//если успешная аутентификация
    private static final String AUTHERR_CMD_PREFIX = "/autherr";//если ошибка при аутентификации
    private static final String PRIVATE_MSG_PREFIX = "/w"; //
    private static final String CLIENT_MSG_PREFIX = "/clientMsg";//сообщение пользователя
    private static final String SERVER_MSG_PREFIX = "/serverMsg";//серверное сообщение
    private static final String END_CMD = "/end"; //остановка сервер*/

    private static final int SERVER_PORT = 8189;
    private static final String SERVER_HOST = "localhost";

    private final int port;
    private final String host;


    private ObjectOutputStream dataOutputStream;
    private ObjectInputStream dataInputStream;


    private Socket socket;
    private String username;
    public Network() {
        this(SERVER_HOST, SERVER_PORT);
    }

    public Network(String serverHost, int serverPort) {
        this.host = serverHost;
        this.port = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            dataInputStream= new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Соединение не было установлено");
            e.printStackTrace();
            return false;
        }


    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {


        }
    }



    public void waitMessage(MainWindowController mainWindowController) {
        Thread thread = new Thread( () -> {
            try {
                while (true) {

                    Command command = readCommand();
                    if(command == null) {
                       Client.showErrorMessage("Error","Ошибка серверва", "Получена неверная команда");
                        continue;
                    }

                    switch (command.getType()) {
                        case INFO_MESSAGE: {
                            MessageInfoCommandData data = (MessageInfoCommandData) command.getData();
                            String message = data.getMessage();
                            String sender = data.getSender();
                            String formattedMessage = sender != null ? String.format("%s: %s", sender, message) : message;
                            Platform.runLater(() -> {
                                try {
                                    mainWindowController.appendMessage(formattedMessage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            break;
                        }
                        case ERROR: {
                            ErrorCommandData data = (ErrorCommandData) command.getData();
                            String errorMessage = data.getErrorMessage();
                            Platform.runLater(() -> {
                                Client.showErrorMessage("Error", "Server error", errorMessage);
                            });
                            break;
                        }
                        case UPDATE_USERS_LIST: {
                            UpdateUsersListCommandData data = (UpdateUsersListCommandData) command.getData();
                            Platform.runLater(() -> mainWindowController.updateUsers(data.getUsers()));
                            break;
                        }
                        default:
                            Platform.runLater(() -> {
                                Client.showErrorMessage("Error","Unknown command from server!", command.getType().toString());
                            });
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Соединение потеряно!");
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login, String password) {
        try {
            Command authCommand = Command.authCommand(login, password);
            dataOutputStream.writeObject(authCommand);

            Command command = readCommand();
            if (command == null) {
                return "Ошибка чтения команды с сервера";
            }

            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    this.username = data.getUsername();
                    return null;
                }

                case AUTH_ERROR:
                case ERROR: {
                    AuthErrorCommandData data = (AuthErrorCommandData) command.getData();
                    return data.getErrorMessage();
                }
                default:
                    return "Неизвестный тип команды: " + command.getType();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(Command.publicMessageCommand(username, message));
    }
    public void sendMessage(Command command) throws IOException {
        dataOutputStream.writeObject(command);
    }



    public void sendPrivateMessage(String message, String recipient) throws IOException {
        Command command = Command.privateMessageCommand(recipient, message);
        sendMessage(command);
    }

    private Command readCommand() throws IOException {
        try {
            return (Command) dataInputStream.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Получен неизвестный объект";
            System.err.println(errorMessage);
            e.printStackTrace();
            sendMessage(Command.errorCommand(errorMessage));
            return null;
        }
    }
}
