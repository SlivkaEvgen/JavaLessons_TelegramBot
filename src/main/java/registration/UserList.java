package registration;

import java.util.HashMap;
import java.util.Map;

public class UserList {

    private static final Map<Long, User> users = new HashMap<>();

    public static void newUser(long chatId) {
        users.put(chatId, new User(chatId));
    }

    public static void addEmail(long chatId, String email) {
        users.get(chatId).setEmail(email);
    }

    public static void addGroup(long chatId, String groupName) {
        users.get(chatId).setGroupName(groupName);
    }

    public static String getEmail(long chatId) {
        return users.get(chatId).getEmail();
    }

    public static String getGroup(long chatId) {
        return users.get(chatId).getGroupName();
    }

    public static int getCurrentQuestion(long chatId) {
        return users.get(chatId).getCurrentQuestion();
    }

    public static boolean isUserExist(long chatId) {
        return users.containsKey(chatId);
    }

    public static void setCurrentQuestion(long chatId, int currentQuestion) {
        users.get(chatId).setCurrentQuestion(currentQuestion);
    }
}
