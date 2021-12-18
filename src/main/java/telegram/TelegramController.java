package telegram;

import java.io.Serializable;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface TelegramController {

    <T extends Serializable, Method extends BotApiMethod<T>> T sendMethod(Method method);

}