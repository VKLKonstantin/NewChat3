package models;

import controllers.MainWindowController;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Network {//отвечает за связь с сервером
    //подключение с сервером
    //ожидание сообщений
    //отправка сообщений

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8189;

    private final String host;
    private final int port;

    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    public Network() {
        this(SERVER_HOST, SERVER_PORT);
    }

    public Network(String serverHost, int serverPort) {
        this.host = serverHost;
        this.port = serverPort;
    }


    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Соединение не установлено");
            return false;
        }


    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {


        }
    }


    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void waitMessage(MainWindowController mainWindowController) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    mainWindowController.appendMessage("Я: " + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
             //   Client.showErrorMessage("Ошибка подключения", "", e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
