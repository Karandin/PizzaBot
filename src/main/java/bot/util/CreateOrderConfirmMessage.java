package bot.util;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

public class CreateOrderConfirmMessage {

    public SendMessage createMessage(long chatid,int id, String name, String tgName, String phone, String address, StringBuilder order, int sum) {
        return new SendMessage(chatid,"Заказ №" + id + "\nName: " + name + "\nTelegram Name: " +
                tgName + "\nPhone: " + phone + "\nAddress: " + address + "\n\n" + order + "\nTotal: " + sum).replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("Принять в работу").callbackData("deliveryConfirm " + chatid + " " +
                       id)));
    }
}
