import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен, ждем подключения...");
            socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String message = in.readUTF();
                System.out.println("Сообщение пользователя " + message);
                if (message.equals("/exit")) {
                    break;
                }
                out.writeUTF("Ответ от сервера " + message.toUpperCase());
            }

        }
    }

    //список клиентов
    //управление соединением с клиентом и сервером
    //рассылка сообщений

}
