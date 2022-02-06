package org.telegram.registration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.apache.commons.validator.routines.EmailValidator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.studyblock.StudyBlock;
import org.telegram.bot.TelegramController;
import org.telegram.bot.TelegramControllerImpl;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Registration {

    private final HashMap<Long, String> requestBase = new HashMap<>();
    private final HashMap<Long, User> tempUser = new HashMap<>();
    private final ArrayList<User> tempUserBase = new ArrayList<>();

    private static final String DESTINATION = "UserBase/user.json";
    private static final String GROUP_NAME_REQUEST = "Group";
    private static final String USER_NAME_REQUEST = "Name";
    private static final String USER_SURNAME_REQUEST = "Surname";
    private static final String STUDY_START_REQUEST = "Go";

    public void handleTextUpdate(Update update, TelegramControllerImpl telegramController) { //обработка "/start" и текстового сообщения от пользователя при наличии запроса от бота
        long chatId = update.getMessage().getChatId();
        if (update.getMessage().getText().equals("/start")) {
            startMessage(update, telegramController);
        } else {
            String lastRequest = requestBase.get(chatId);
            switch (lastRequest) {
                case "Enter":
                    enterRequest(update, telegramController);
                    break;
                case "Registration":
                    emailRegistrar(update, telegramController);
                    break;
                case GROUP_NAME_REQUEST:
                    groupRegistrar(update, telegramController);
                    break;
                case USER_NAME_REQUEST:
                    nameRegistrar(update, telegramController);
                    break;
                case USER_SURNAME_REQUEST:
                    surnameRegistrar(update, telegramController);
                    break;
            }
        }
    }

    public void handleCallbackQuery(Update update, TelegramControllerImpl telegramController) { //реакции на колбеки по кнопкам
        long chatId = update.getCallbackQuery().getFrom().getId();
        String callbackQuery = update.getCallbackQuery().getData();

        switch (callbackQuery) {
            case "Registration":
                requestBase.put(chatId, "Registration");

                InputStream in = StudyBlock.class.getResourceAsStream("/sticker3.webp");

                SendSticker sticker = new SendSticker();
                sticker.setChatId(String.valueOf(chatId));
                InputFile stickerFile = new InputFile(in, "sticker3.webp");
                sticker.setSticker(stickerFile);

                try {
                    telegramController.execute(sticker);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                sendText(chatId, "Регистрирую новичка))\nУкажи e-mail, по которому я смогу узнавать тебя", 0, telegramController);
                break;
            case "Enter":
                requestBase.put(chatId, "Enter");

                InputStream in2 = StudyBlock.class.getResourceAsStream("/sticker3.webp");

                SendSticker sticker2 = new SendSticker();
                sticker2.setChatId(String.valueOf(chatId));
                InputFile stickerFile2 = new InputFile(in2, "sticker3.webp");
                sticker2.setSticker(stickerFile2);

                try {
                    telegramController.execute(sticker2);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                sendText(chatId, "Введи адрес электронной почты", 0, telegramController);
                break;
            case "Go":
                sendText(chatId, "Гоу учиться! Я создал!", 0, telegramController);

                InputStream in3 = StudyBlock.class.getResourceAsStream("/sticker5.webp");

                SendSticker sticker3 = new SendSticker();
                sticker3.setChatId(String.valueOf(chatId));
                InputFile stickerFile3 = new InputFile(in3, "sticker5.webp");
                sticker3.setSticker(stickerFile3);

                try {
                    telegramController.execute(sticker3);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                //сюда можно дописать логику старта обучения
                break;
        }
    }

    private void sendText(long chatId, String messageText, int isKeyboard, TelegramController telegramController) { //отправка сообщения пользователю
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(Long.toString(chatId));
        sendMessageRequest.setText(messageText);
        switch (isKeyboard) {
            case 1:
                sendMessageRequest.setReplyMarkup(createRegistrationKeyboard());
                break;
            case 2:
                sendMessageRequest.setReplyMarkup(createStartKeyboard());
        }
        telegramController.sendMethod(sendMessageRequest);
    }

    private ReplyKeyboard createRegistrationKeyboard() { //клавиатура для регистрации
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(
                Collections.singletonList(
                        Arrays.asList(
                                InlineKeyboardButton.builder().text("Регистрация").callbackData("Registration").build(),
                                InlineKeyboardButton.builder().text("Вход").callbackData("Enter").build()
                        )
                )
        );
        return keyboard;
    }

    private ReplyKeyboard createStartKeyboard() { //клавиатура для подтверждения готовности старта обучения
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(
                Collections.singletonList(
                        Collections.singletonList(
                                InlineKeyboardButton.builder().text("Старт").callbackData("Go").build()
                        )
                )
        );
        return keyboard;
    }

    private void startMessage(Update update, TelegramControllerImpl telegramController) { //ответ на первичный старт
        long chatId = update.getMessage().getChatId();

        InputStream in = StudyBlock.class.getResourceAsStream("/sticker2.webp");

        SendSticker sticker = new SendSticker();
        sticker.setChatId(String.valueOf(chatId));
        InputFile stickerFile = new InputFile(in, "sticker2.webp");
        sticker.setSticker(stickerFile);

        try {
            telegramController.execute(sticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        String messageText = "Если ты здесь впервые - нажимай Регистрация.\n" +
                "Если ты уже тёртый калач - продолжаем. Жми Вход!";
        sendText(chatId, messageText, 1, telegramController);
    }

    private void enterRequest(Update update, TelegramController telegramController) { //проверка e-mail перед входом
        long chatId = update.getMessage().getChatId();
        String email = update.getMessage().getText().trim();
        if (isEmailValid(email)) {
            if (isNoUserCoincidence(email)) {
                sendText(chatId, "Пользователь с таким e-mail не зарегистрирован. Попробуй ще раз или пройдите регистрацию", 1, telegramController);
            } else {
                sendText(chatId, "Вход выполнен.\nЕсли готов учиться - жми Старт!", 2, telegramController);
            }
        } else {
            sendText(chatId, "Адрес электронной почты указан неверно. Попробуй еще раз", 1, telegramController);
        }
    }

    private void emailRegistrar(Update update, TelegramControllerImpl telegramController) { //регистрация e-mail
        long chatId = update.getMessage().getChatId();
        String email = update.getMessage().getText().trim();
        if (isEmailValid(email)) {
            if (!isNoUserCoincidence(email)) {

                InputStream in = StudyBlock.class.getResourceAsStream("/sticker4.webp");

                SendSticker sticker = new SendSticker();
                sticker.setChatId(String.valueOf(chatId));
                InputFile stickerFile = new InputFile(in, "sticker4.webp");
                sticker.setSticker(stickerFile);

                try {
                    telegramController.execute(sticker);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                sendText(chatId, "Пользователь с таким e-mail уже зарегистрирован. Попробуй выполнить Вход", 1, telegramController);
            } else {
                User user = new User();
                user.setEmail(email);
                user.setChatId(chatId);
                user.setCurrentQuestion(0);
                user.setCurrentBlock("");
                tempUser.put(chatId, user);
                requestBase.put(chatId, GROUP_NAME_REQUEST);
                sendText(chatId, "Укажи название своей группы", 0, telegramController);
            }
        } else {
            sendText(chatId, "Адрес электронной почты указан неверно. Попробуй еще раз.", 1, telegramController);
        }
    }

    private void groupRegistrar(Update update, TelegramController telegramController) { //регистрация имени группы
        long chatId = update.getMessage().getChatId();
        String group = update.getMessage().getText().trim();
        User user = tempUser.get(chatId);
        user.setGroupName(group);
        tempUser.put(chatId, user);
        requestBase.put(chatId, USER_NAME_REQUEST);
        sendText(chatId, "Укажи своё имя", 0, telegramController);
    }

    private void nameRegistrar(Update update, TelegramController telegramController) { //регистрация имени пользователя
        long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getText().trim();
        User user = tempUser.get(chatId);
        user.setUserName(name);
        tempUser.put(chatId, user);
        requestBase.put(chatId, USER_SURNAME_REQUEST);
        sendText(chatId, "Укажи свою фамилию", 0, telegramController);
    }

    private void surnameRegistrar(Update update, TelegramController telegramController) { //регистрация фамилии пользователя
        long chatId = update.getMessage().getChatId();
        String surName = update.getMessage().getText().trim();
        User user = tempUser.get(chatId);
        user.setUserSurname(surName);
        tempUser.put(chatId, user);
        putUsersToBase(chatId, tempUser);
        requestBase.put(chatId, "Go");
        sendText(chatId, "Регистрация завершена. Если готов учиться - жми Старт!", 2, telegramController);
    }

    private void getUsersFromBase() { //получение пользователей из файла-базы во временное хранилище
        File file = new File(DESTINATION);
        if (!file.exists()) { //если файла-базы еще нет создаем его и пару пустых пользователей в нем для последующего корректного чтения этого файла
            file.getParentFile().mkdir();
            try {
                file.createNewFile();
                //System.out.println("\nСоздан файл " + file.getName() + file.getPath());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            tempUserBase.add(new User.UserBuilder()
                    .email("anon@gmail.com")
                    .groupName("JavaCore2")
                    .chatId(3654L)
                    .currentBlock("Java")
                    .currentQuestion(5)
                    .build()
            );
            write(DESTINATION, tempUserBase);
            tempUserBase.clear();
        } else {
            try (BufferedReader toRead = new BufferedReader(new FileReader(file))) {
                tempUserBase.addAll(Arrays.asList(new Gson().fromJson(toRead.lines().collect(Collectors.joining()), User[].class)));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @SneakyThrows
    public static <T> void write(String fileName, List<T> list) {
        try (FileWriter fileJson = new FileWriter(fileName)) {
            fileJson.write(new Gson().toJson(list));
            //System.out.println("\nДанные записаны в файл " + fileName);
        }
    }

    private void putUsersToBase(long chatId, HashMap<Long, User> tempUser) { //перезапись файла-хранилища с новым пользователем
        if (tempUserBase.isEmpty()) {
            getUsersFromBase();
        }

        tempUserBase.add(tempUser.get(chatId));
        Gson json = new GsonBuilder().create();
        File file = new File(DESTINATION);
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

    private boolean isEmailValid(String email) { //проверка валидности e-mail
        EmailValidator eValidator = EmailValidator.getInstance();
        return eValidator.isValid(email);
    }

    private boolean isNoUserCoincidence(String email) { //проверка отсутствия совпадения переданного e-mail с базой пользователей. Возвращает true при отсутствии совпадений
        if (tempUserBase.isEmpty()) {
            getUsersFromBase();
        }

        long coincidence = tempUserBase.stream()
                .filter(user -> user.getEmail().equals(email))
                .count();
        //System.out.println(coincidence);
        return coincidence == 0;
    }
}