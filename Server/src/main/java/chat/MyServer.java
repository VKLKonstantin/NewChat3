package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;
import chat.handler.ClientHandler;
import clientservice.Command;

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
        clientSocket.setSoTimeout(120000);//отключение неавторизованных пользователей по таймауту
        //   (120 сек. ждем после подключения клиента. Если он не авторизовался за это время, закрываем соединение).
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

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {//добавление клиента
        clients.add(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }
    private List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }
    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        List<String> usernames = getAllUsernames();
        broadcastMessage(null, Command.updateUsersListCommand(usernames));
    }

    public synchronized boolean isUsernameBusy(String username) {//проверка, не занет ли никнейм
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;

    }
    public synchronized void broadcastMessage(ClientHandler sender, Command command) throws IOException {//оповестить всех пользователей о подключении новичка
        for (ClientHandler client : clients) {
            if(client == sender) {//если клиент является отправителем, то мы его игнорируем
                continue;
            }
            client.sendMessage(command);
        }
    }
    public synchronized void sendPrivateMessage(String recipient, Command command) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                client.sendMessage(command);
                break;
            }
        }
    }
}

