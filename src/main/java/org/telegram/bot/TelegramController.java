package org.telegram.bot;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

public interface TelegramController {

    <T extends Serializable, Method extends BotApiMethod<T>> T sendMethod(Method method);

}