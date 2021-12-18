package studyblock;

import java.io.*;
import java.util.ArrayList;
import com.google.gson.Gson;
import googleSheets.GoogleSheetsApiController;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import registration.User;
import registration.UserStudyStatus;
import telegram.TelegramController;
import telegram.TelegramControllerImpl;

public class StudyBlock {

    public static int currentQuestion;
    public static String currentBlock;

    private final List<String> blockQuestions = GoogleSheetsApiController.Questions();
    private final List<String> blockAnswers = GoogleSheetsApiController.Answers();

    private static final ArrayList<User> tempUserBase = new ArrayList<>();
    private static final String FILE_NAME = "UserBase/user.json";

    public void handleCallbackQuery(Update update, TelegramControllerImpl telegramController) { //реакция на нажатие кнопок
        long chatId = update.getCallbackQuery().getFrom().getId();
        String callbackQuery = update.getCallbackQuery().getData();

        switch (callbackQuery) {
            case "answer":
                sendText(chatId, blockAnswers.get(currentQuestion), 0, telegramController);
                break;
            case "next":
                if (currentQuestion<blockQuestions.size()-1){
                currentQuestion++;
                    UserStudyStatus.changeUserStudyStatus(chatId, currentQuestion, currentBlock);
                } else{

                    InputStream in3 = StudyBlock.class.getResourceAsStream("/sticker6.webp");

                    SendSticker sticker3 = new SendSticker();
                    sticker3.setChatId(String.valueOf(chatId));
                    InputFile stickerFile3 = new InputFile(in3,"sticker6.webp");
                    sticker3.setSticker(stickerFile3);

                    try {
                        telegramController.execute(sticker3);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    String messageText = "Ты закончил этот блок!\nВыбери следующий:";
                    sendText(chatId, messageText, 2, telegramController);
                    break;
                }

                String messageText = blockQuestions.get(currentQuestion);
                sendText(chatId, messageText, 1, telegramController);
                break;
        }
    }
    public void handleTextUpdate(Update update, TelegramControllerImpl telegramController) { // обработка Go

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("Go")) {
            if (tempUserBase.isEmpty()) {
                getUsersFromBase();
            }

            int index = 0;

            for (int i = 0; i < tempUserBase.size(); i++) {
                long tempChatId = tempUserBase.get(i).getChatId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                if (tempChatId == chatId) {
                    index = i;
                }
            }

            if (!tempUserBase.get(index).getCurrentBlock().isEmpty() && tempUserBase.get(index).getCurrentQuestion() != 0) {
                currentQuestion = tempUserBase.get(index).getCurrentQuestion();
                currentBlock = tempUserBase.get(index).getCurrentBlock();
                List<String> blockQuestions = GoogleSheetsApiController.Questions();
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                sendText(chatId, "Продолжим с места, где остановились!\n Раздел " + currentBlock, 0, telegramController);
                String messageText = blockQuestions.get(currentQuestion);
                sendText(chatId, messageText, 1, telegramController);
            }
            else {
                long chatId = update.getCallbackQuery().getFrom().getId();
                String messageText = "Выбери интересующий тебя блок:";
                sendText(chatId, messageText, 2, telegramController);
            }
            tempUserBase.clear();
        }

        if (update.hasMessage() && update.getMessage().getText().equals("⏺ Java")) {
            currentBlock = "⏺ Java";
            currentQuestion = 1;
            UserStudyStatus.changeUserStudyStatus(update.getMessage().getChatId(), currentQuestion, currentBlock);
            List<String> blockQuestions = GoogleSheetsApiController.Questions();
            long chatId = update.getMessage().getChatId();

            InputStream in = StudyBlock.class.getResourceAsStream("/sticker.webp");

            SendSticker sticker = new SendSticker();
            sticker.setChatId(String.valueOf(chatId));
            InputFile stickerFile = new InputFile(in,"sticker1.webp");
            sticker.setSticker(stickerFile);

            try {
                telegramController.execute(sticker);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            sendText(chatId, "Прекрасный выбор!", 0, telegramController);
            String messageText = blockQuestions.get(currentQuestion);
            sendText(chatId, messageText, 1, telegramController);
        }
    }

    private void sendText(long chatId, String messageText, int isKeyboard, TelegramController telegramController) { //отправка сообщения пользователю
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(Long.toString(chatId));
        sendMessageRequest.setText(messageText);
        switch (isKeyboard) {
            case 1:
                sendMessageRequest.setReplyMarkup(createUnitKeyboard());
                break;
            case 2:
                sendMessageRequest.setReplyMarkup(createBlockKeyboard());
        }
        telegramController.sendMethod(sendMessageRequest);
    }
    @SneakyThrows
    private ReplyKeyboard createBlockKeyboard() {
        List<String> blocksNames = GoogleSheetsApiController.Blocks();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String blocksName : blocksNames) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add("⏺ " + blocksName);
            keyboard.add(keyboardRow);
        }

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("/settings");
        keyboard.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboard createUnitKeyboard() { //клавиатура для юнитов
        List<String> blockVideos = GoogleSheetsApiController.Videos();
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(
                Collections.singletonList(
                        Arrays.asList(
                                InlineKeyboardButton.builder().text("\uD83D\uDD3D ответ").callbackData("answer").build(),
                                InlineKeyboardButton.builder().text("\uD83C\uDFA6 видео").url(blockVideos.get(currentQuestion)).callbackData("video").build(),
                                InlineKeyboardButton.builder().text("▶️next").callbackData("next").build()
                        )
                )
        );
        return keyboard;
    }

    private static void getUsersFromBase() { //получение пользователей из файла-базы во временное хранилище
        File file = new File(FILE_NAME);

        try (BufferedReader toRead = new BufferedReader(new FileReader(file))) {
            tempUserBase.addAll(Arrays.asList(new Gson().fromJson(toRead.lines().collect(Collectors.joining()), User[].class)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}