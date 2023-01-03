package bot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;

public class Keyboards {

    public InlineKeyboardMarkup productKb() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("\uD83C\uDD99Размер").callbackData("size")).
                addRow(new InlineKeyboardButton("\uD83C\uDF7DМеню").switchInlineQueryCurrentChat(""));
    }

    public InlineKeyboardMarkup addToOrderKb(int amount) {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("-").
                callbackData("minus"), new InlineKeyboardButton(String.valueOf(amount)).callbackData("dummy"), new InlineKeyboardButton("+").
                callbackData("plus")).addRow(new InlineKeyboardButton("➕Добавить").callbackData("add")).
                addRow(new InlineKeyboardButton("\uD83D\uDDD1Удалить").callbackData("delete"));
    }

    public EditMessageReplyMarkup sizeKb(Long chatId, int messageId, String bigDisc, String smallDisc) {
        return new EditMessageReplyMarkup(chatId, messageId).replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton(smallDisc + "+ 0₽").callbackData("small")).addRow(
                new InlineKeyboardButton(bigDisc + "+ 40₽").callbackData("big")));
    }

    public InlineKeyboardMarkup continueKb() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Продолжить выбор").switchInlineQueryCurrentChat("")).addRow(
                new InlineKeyboardButton("Корзина").callbackData("cart"));
    }

    public InlineKeyboardMarkup cartKb(int amount) {
        return new InlineKeyboardMarkup(new InlineKeyboardButton("-").
                callbackData("minusCart"), new InlineKeyboardButton(String.valueOf(amount)).callbackData("dummy"),
                new InlineKeyboardButton("+").callbackData("plusCart")).
                addRow(new InlineKeyboardButton("\uD83D\uDDD1Удалить").callbackData("deleteCart"));
    }

    public InlineKeyboardMarkup resultKb() {

        return new InlineKeyboardMarkup(new InlineKeyboardButton("Оформить заказ").
                callbackData("confirm")).addRow(new InlineKeyboardButton("Добавить позиции").switchInlineQueryCurrentChat(""));
    }
}
