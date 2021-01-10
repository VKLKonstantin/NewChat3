package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;
import chat.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
//ожидание нового подключения
    //хранение всех пользователей
    //ServerSocket

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }

    public void start() throws IOException {
        System.out.println("Сервер запущен");
        authService.start();

        try {
            while (true) {
                waitNewClientConnection();
            }
        } catch (IOException e) {
            System.out.println("Ошибка создания нового подключения");
        } finally {
            serverSocket.close();
        }
    }

    private void waitNewClientConnection() throws IOException {
        System.out.println("Ожидание клинта");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Клиент подключился");
        processConnectionClient(clientSocket);
    }

    private void processConnectionClient(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();

    }

    public AuthService getAuthService() {
        return authService;
    }

    public void subscribe(ClientHandler clientHandler) {//добавление клиента
        clients.add(clientHandler);
    }

    public void unSubscribe(ClientHandler clientHandler) {//удаление клиента
        clients.remove(clientHandler);
    }

    public boolean isUsernameBusy(String username) {//проверка, не занет ли никнейм
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;

    }
    public void broadcastMessage(String message, ClientHandler sender, boolean isServerInfoMsg) throws IOException {//оповестить всех пользователей о подключении новичка
        for (ClientHandler client : clients) {
            if(client == sender) {//если клиент является отправителем, то мы его игнорируем
                continue;
            }
            client.sendMessage(isServerInfoMsg ? null : sender.getUsername(), message);//если флаг(isServerInfoMsg) true, то серверное сообщение
            //иначе передается имя пользователя
        }
    }
}

