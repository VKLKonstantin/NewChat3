package chat.auth;

import chat.User;

import java.util.List;

public class BaseAuthService implements AuthService {

    private static final List<User> clients = List.of(
            new User("user1", "1111", "Костя"),
            new User("user2", "2222", "Надя"),
            new User("user3", "3333", "Гендальф_Серый")
    );

    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for(User client: clients){
if(client.getLogin().equals(login)&&client.getPassword().equals(password)){
    return client.getUsername();
}
        }
        String s="Вы не зарегистрированы";
        return s;
    }

    @Override
    public void close() {

    }
}
