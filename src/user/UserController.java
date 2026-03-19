package user;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    private static List<User> users = new ArrayList<>();

    public static void createUser(User user) {
        users.add(user);
    }

    public static User getUser(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public static void deleteUser(String userId) {
        users.removeIf(user -> user.getUserId().equals(userId));
    }
}