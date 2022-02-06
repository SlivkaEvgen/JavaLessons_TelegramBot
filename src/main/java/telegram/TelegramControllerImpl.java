package telegram;

import java.io.Serializable;

import lombok.SneakyThrows;
import notification.Settings;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import registration.Registration;
import studyblock.StudyBlock;

public abstract class TelegramControllerImpl extends TelegramLongPollingBot implements TelegramController {

    private final Registration registration = new Registration();
    private final StudyBlock studyBlock = new StudyBlock();
    private final Settings settings = new Settings();

    @Override
    public String getBotUsername() {
        return TelegramConstants.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return TelegramConstants.BOT_TOKEN;
    }

    @SneakyThrows
    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T sendMethod(Method method) {
        return super.sendApiMethod(method);
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/settings")) {
            settings.handleTextUpdate(update, this);
        }

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/start")) {
            registration.handleTextUpdate(update, this);
        } else if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().startsWith("⏺ ") && !update.getMessage().getText().startsWith("\uD83D") && !update.getMessage().getText().startsWith("/settings")) {

            System.out.println(update.getMessage().getText());
            registration.handleTextUpdate(update, this);
        }
        if (update.hasCallbackQuery() && !update.getCallbackQuery().getData().startsWith("answer")) {
            registration.handleCallbackQuery(update, this);
        }

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("⏺ Java")) {
            studyBlock.handleTextUpdate(update, this);
        }

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("Go")) {
            studyBlock.handleTextUpdate(update, this);
        }

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("answer")) {
            studyBlock.handleCallbackQuery(update, this);
        }

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().startsWith("next")) {
            studyBlock.handleCallbackQuery(update, this);
        }

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("\uD83D")) {
            settings.handleCallbackQuery(update, this);
        }
    }
}