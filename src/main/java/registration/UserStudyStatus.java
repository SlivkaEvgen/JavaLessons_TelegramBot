package registration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//статический утилитный класс. Единственный публичный метод получает на вход e-mail (для идентификации пользователя в базе) и новые данные по номеру и названию главы.
public class UserStudyStatus {

    private static final String FILE_NAME = "UserBase/user.json";

    private static final ArrayList<User> tempUserBase = new ArrayList<>();

    public static void changeUserStudyStatus(long chatId, int currentQuestion, String currentBlock) {
        if (tempUserBase.isEmpty()) {
            getUsersFromBase();
        }
        if (isUserExists(chatId)) {
            int index = 0;
            for (int i = 0; i < tempUserBase.size(); i++) {
                if (tempUserBase.get(i).getChatId() == chatId) {
                    index = i;
                }
            }
            tempUserBase.get(index).setCurrentQuestion(currentQuestion);
            tempUserBase.get(index).setCurrentBlock(currentBlock);
            putUsersToBase();
        }
//        else {
//            //System.out.println("Пользователь не найден в базе");
//        }
    }

    private static boolean isUserExists(long chatId) { //проверка переданного chatId в базе пользователей. Возвращает true при совпадении
        long coincidence = tempUserBase.stream()
                .filter(user -> user.getChatId() == chatId)
                .count();
        return coincidence != 0;
    }

    private static void getUsersFromBase() { //получение пользователей из файла-базы во временное хранилище
        File file = new File(FILE_NAME);

        try (BufferedReader toRead = new BufferedReader(new FileReader(file))) {
            tempUserBase.addAll(Arrays.asList(new Gson().fromJson(toRead.lines().collect(Collectors.joining()), User[].class)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void putUsersToBase() { //перезапись базы с измененным пользователем
        Gson json = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FILE_NAME);

        try (BufferedWriter toWrite = new BufferedWriter(new FileWriter(file))) {
            List<String> jsonArr = tempUserBase.stream()
                    .map(json::toJson)
                    .collect(Collectors.toList());
            toWrite.append(jsonArr.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        tempUserBase.clear();
    }
}