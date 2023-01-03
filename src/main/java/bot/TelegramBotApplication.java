package bot;

import bot.model.Client;
import bot.model.DeliveryStatus;
import bot.model.Order;
import bot.model.Pizza;
import bot.util.CreateOrderConfirmMessage;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TelegramBotApplication extends TelegramBot {
    Keyboards kb = new Keyboards();
    HashMap<Long, Order> orderMap = new HashMap();
    HashMap<Long, List<Order>> cartMap = new HashMap();
    HashMap<Long, DeliveryStatus> deliveryMap = new HashMap();
    HashMap<Long, Client> clientMap = new HashMap();
    private int orderId = 1;
    Order ord = new Order();
    SaveOrders saveOrder = new SaveOrders();
    CreateOrderConfirmMessage orderMessage = new CreateOrderConfirmMessage();

    Pizza p1 = new Pizza("Мексиканская", "Нежная, 33см (720г)", "Нежная, 45см (1100г)",
            "Состав: Мука, соль и любовь", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxH8Gs-0DtKhZrnscDhRMxLeEx9PMI8gsukA&usqp=CAU", 100, 40, "1", false);
    Pizza p2 = new Pizza("Пеперони", "Нежная, 33см (720г)", "Нежная, 45см (1100г)",
            "Состав: другой состав", "https://www.gastronom.ru/binfiles/images/20220420/b1fd4150.jpg", 150, 40, "2", false);
    InlineQueryResultPhoto go = new InlineQueryResultPhoto(p1.getId(), p1.getPhoto(), p1.getPhoto());

    public InlineQueryResultArticle r1 = new InlineQueryResultArticle(p1.getId(), p1.getName() + " " + p1.getPrice() + "₽", "newOrder").
            thumbUrl(p1.getPhoto()).description(p1.getDescription() + "\n" + p1.getComposition());
    public InlineQueryResultArticle r2 = new InlineQueryResultArticle(p2.getId(), p2.getName() + " " + p2.getPrice() + "₽", "newOrder").
            thumbUrl(p2.getPhoto()).description(p2.getDescription() + "\n" + p2.getComposition());

    @lombok.Builder
    public TelegramBotApplication(String botToken) {
        super(botToken);
    }

    public void run() {
        this.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {

        Message message = update.message();
        InlineQuery inlineQuery = update.inlineQuery();
        CallbackQuery callbackQuery = update.callbackQuery();
        try {
            if (callbackQuery != null) {

                String data = callbackQuery.data();

                switch (data) {
                    case "size" -> {
                        this.execute(kb.sizeKb(callbackQuery.from().id(), callbackQuery.message().messageId(), orderMap.get(
                                callbackQuery.from().id()).getPizza().getBigDescription(), orderMap.get(
                                callbackQuery.from().id()).getPizza().getDescription()));
                    }
                    case "small" -> {
                        orderMap.get(callbackQuery.from().id()).getPizza().setBig(false);
                        int sum = ord.calculateOrderPrice(orderMap.get(callbackQuery.from().id()).getPizza().getPrice(),
                                orderMap.get(callbackQuery.from().id()).getAmount(),
                                0, false);
                        orderMap.get(callbackQuery.from().id()).setOrderPrice(sum);

                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                orderMap.get(callbackQuery.from().id()).getPizza().getDescription() + "\n" +
                                        orderMap.get(callbackQuery.from().id()).getPizza().getComposition() + "\n" +
                                        "Цена: " + sum + "₽");

                        this.execute(response.replyMarkup(kb.addToOrderKb(orderMap.get(callbackQuery.from().id()).getAmount())));
                    }
                    case "big" -> {
                        orderMap.get(callbackQuery.from().id()).getPizza().setBig(true);
                        int sum = ord.calculateOrderPrice(orderMap.get(callbackQuery.from().id()).getPizza().getPrice(),
                                orderMap.get(callbackQuery.from().id()).getAmount(),
                                orderMap.get(callbackQuery.from().id()).getPizza().getPriceBig(), true);
                        orderMap.get(callbackQuery.from().id()).setOrderPrice(sum);

                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                orderMap.get(callbackQuery.from().id()).getPizza().getBigDescription() + "\n" +
                                        orderMap.get(callbackQuery.from().id()).getPizza().getComposition() + "\n" +
                                        "Цена: " + sum + "₽");

                        this.execute(response.replyMarkup(kb.addToOrderKb(orderMap.get(callbackQuery.from().id()).getAmount())));
                    }
                    case "minus" -> {
                        int amount = orderMap.get(callbackQuery.from().id()).getAmount();
                        if (amount > 1) {
                            orderMap.get(callbackQuery.from().id()).setAmount(amount - 1);
                        }
                        int sum = ord.calculateOrderPrice(orderMap.get(callbackQuery.from().id()).getPizza().getPrice(),
                                orderMap.get(callbackQuery.from().id()).getAmount(),
                                orderMap.get(callbackQuery.from().id()).getPizza().getPriceBig(), orderMap.get(callbackQuery.from().id()).getPizza().isBig());

                        orderMap.get(callbackQuery.from().id()).setOrderPrice(sum);

                        String desc = "";
                        if (orderMap.get(callbackQuery.from().id()).getPizza().isBig()) {
                            desc = orderMap.get(callbackQuery.from().id()).getPizza().getBigDescription();
                        } else {
                            desc = orderMap.get(callbackQuery.from().id()).getPizza().getDescription();
                        }
                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                desc + "\n" + orderMap.get(callbackQuery.from().id()).getPizza().getComposition() + "\n" +
                                        "Цена: " + sum + "₽");
                        this.execute(response.replyMarkup(kb.addToOrderKb(orderMap.get(callbackQuery.from().id()).getAmount())));
                    }
                    case "plus" -> {
                        int amount = orderMap.get(callbackQuery.from().id()).getAmount();
                        orderMap.get(callbackQuery.from().id()).setAmount(amount + 1);

                        int sum = ord.calculateOrderPrice(orderMap.get(callbackQuery.from().id()).getPizza().getPrice(),
                                orderMap.get(callbackQuery.from().id()).getAmount(), orderMap.get(callbackQuery.from().id()).getPizza().getPriceBig(),
                                orderMap.get(callbackQuery.from().id()).getPizza().isBig());

                        orderMap.get(callbackQuery.from().id()).setOrderPrice(sum);

                        String desc = "";
                        if (orderMap.get(callbackQuery.from().id()).getPizza().isBig()) {
                            desc = orderMap.get(callbackQuery.from().id()).getPizza().getBigDescription();
                        } else {
                            desc = orderMap.get(callbackQuery.from().id()).getPizza().getDescription();
                        }

                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                desc + "\n" + orderMap.get(callbackQuery.from().id()).getPizza().getComposition() + "\n" +
                                        "Цена: " + sum + "₽");
                        this.execute(response.replyMarkup(kb.addToOrderKb(orderMap.get(callbackQuery.from().id()).getAmount())));
                    }
                    case "add" -> {
                        List<Order> oList = new ArrayList<>();
                        oList.add(orderMap.get(callbackQuery.from().id()));
                        if (cartMap.get(callbackQuery.from().id()) == null) {
                            cartMap.put(callbackQuery.from().id(), oList);
                        } else {
                            cartMap.get(callbackQuery.from().id()).add(orderMap.get(callbackQuery.from().id()));
                        }
                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                "✔️Добавлено в корзину").replyMarkup(kb.continueKb());
                        this.execute(response);
                    }
                    case "delete" -> {
                        orderMap.remove(callbackQuery.from().id());
                        EditMessageCaption response = new EditMessageCaption(callbackQuery.from().id(), callbackQuery.message().messageId()).caption(
                                "Может что-нибудь другое?").replyMarkup(kb.continueKb());
                        this.execute(response);
                    }
                    case "cart" -> {
                        List<Order> orderList = cartMap.get(callbackQuery.from().id());
                        int count = 1;
                        int sum = 0;
                        for (Order list : orderList) {
                            list.setMessageId(callbackQuery.message().messageId() + count);
                            list.setResultMessageId(callbackQuery.message().messageId() + orderList.size() + 1);
                            sum += list.getOrderPrice();
                            this.execute(new SendPhoto(callbackQuery.from().id(), list.getPizza().getPhoto()).caption(count + ". " +
                                    list.getPizza().getName() + "\nЦена: " + list.getOrderPrice() + "\nЕщё какая-то инфа").replyMarkup(kb.cartKb(list.getAmount())
                            ));
                            count++;
                        }
                        this.execute(new SendMessage(callbackQuery.from().id(), "Сумма: " + sum).replyMarkup(kb.resultKb()));
                        // AnswerCallbackQuery answer = new AnswerCallbackQuery(callbackQuery.id());
                        // this.execute(answer.text("В корзине пока пусто\uD83E\uDD24").showAlert(true));
                    }
                    case "deleteCart" -> {
                        int resMesId = 0;
                        this.execute(new DeleteMessage(callbackQuery.from().id(), callbackQuery.message().messageId()));
                        List<Order> orderList = cartMap.get(callbackQuery.from().id());
                        resMesId = orderList.get(0).getResultMessageId();
                        orderList.removeIf(list -> list.getMessageId() == callbackQuery.message().messageId());
                        int count = 1;
                        int sum = 0;
                        for (Order list : orderList) {
                            this.execute(new EditMessageCaption(callbackQuery.from().id(), list.getMessageId()).caption(count + ". " +
                                    list.getPizza().getName()).replyMarkup(kb.cartKb(list.getAmount())));
                            sum += list.getOrderPrice();
                            count++;
                        }
                        cartMap.put(callbackQuery.from().id(), orderList);
                        if (orderList.size() == 0) {
                            this.execute(new EditMessageText(callbackQuery.from().id(), resMesId,
                                    "В корзине ничего не осталось\uD83D\uDE15").replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("Меню").switchInlineQueryCurrentChat(""))));
                        } else {
                            this.execute(new EditMessageText(callbackQuery.from().id(), resMesId, "Сумма: " + sum).replyMarkup(kb.resultKb()));
                        }
                    }
                    case "minusCart" -> {
                        List<Order> orderList = cartMap.get(callbackQuery.from().id());
                        int sum = 0;
                        int resMesId = 0;
                        for (Order list : orderList) {
                            if (list.getMessageId() == callbackQuery.message().messageId()) {
                                int amount = list.getAmount();
                                if (amount > 1) {
                                    amount--;
                                }
                                list.setAmount(amount);
                                int ordPrice = ord.calculateOrderPrice(list.getPizza().getPrice(), list.getAmount(), list.getPizza().getPriceBig(),
                                        list.getPizza().isBig());
                                list.setOrderPrice(ordPrice);
                                this.execute(new EditMessageCaption(callbackQuery.from().id(), list.getMessageId()).caption(
                                                list.getPizza().getName() + "\nЦена: " + ordPrice + "\nЕщё какая-то инфа").
                                        replyMarkup(kb.cartKb(list.getAmount())));
                            }
                            sum += list.getOrderPrice();
                            resMesId = list.getResultMessageId();
                        }
                        cartMap.put(callbackQuery.from().id(), orderList);
                        this.execute(new EditMessageText(callbackQuery.from().id(), resMesId, "Сумма: " + sum).replyMarkup(kb.resultKb()));
                    }
                    case "plusCart" -> {
                        List<Order> orderList = cartMap.get(callbackQuery.from().id());
                        int sum = 0;
                        int resMesId = 0;
                        for (Order list : orderList) {
                            if (list.getMessageId() == callbackQuery.message().messageId()) {
                                int amount = list.getAmount();
                                list.setAmount(amount + 1);
                                int ordPrice = ord.calculateOrderPrice(list.getPizza().getPrice(), list.getAmount(), list.getPizza().getPriceBig(),
                                        list.getPizza().isBig());
                                list.setOrderPrice(ordPrice);
                                this.execute(new EditMessageCaption(callbackQuery.from().id(), list.getMessageId()).caption(
                                        list.getPizza().getName() + "\nЦена: " + ordPrice + "\nЕщё какая-то инфа").replyMarkup(
                                        kb.cartKb(list.getAmount())));
                            }
                            sum += list.getOrderPrice();
                            resMesId = list.getResultMessageId();
                        }
                        cartMap.put(callbackQuery.from().id(), orderList);
                        this.execute(new EditMessageText(callbackQuery.from().id(), resMesId, "Сумма: " + sum).replyMarkup(kb.resultKb()));
                    }
                    case "confirm" -> {
                        deliveryMap.put(callbackQuery.from().id(), new DeliveryStatus(true, false, false));
                        this.execute(new SendMessage(callbackQuery.from().id(), "Для продолжения введите номер телефона." +
                                " Пример: +79150000000\nИли нажмите на кнопку «Отправить контакт»\uD83D\uDC47").replyMarkup(new ReplyKeyboardMarkup(
                                new KeyboardButton("Отправить контакт").requestContact(true)).resizeKeyboard(true).
                                oneTimeKeyboard(true)));
                    }
                }
                if (data.contains("deliveryConfirm")) {
                   this.execute(new EditMessageReplyMarkup(callbackQuery.from().id(),callbackQuery.message().messageId()).replyMarkup(
                           new InlineKeyboardMarkup(new InlineKeyboardButton("\uD83D\uDC68\u200D\uD83C\uDF73").callbackData("dummy"))
                   ));
                    LocalTime now = LocalTime.now();
                    LocalTime date = now.plusHours(1);

                    String[] arr = data.split(" ");
                    this.execute(new SendMessage(callbackQuery.from().id(),
                            "Нужно доставить до: " + date.format(DateTimeFormatter.ofPattern("HH:mm"))));
                    this.execute(new SendSticker(arr[1], "CAACAgIAAxkBAAEHG_1jtCOpNyrNmHEGgLjFktZMWZtMXAACTwADWbv8JXAeFS_YqOxqLQQ"));
                    this.execute(new SendMessage(arr[1], "Ваш заказ №" + arr[2] + " готовится. Пожалуйста ожидайте доставку в течении 59 минут"));
                }

            }
        } catch (NullPointerException e) {
            this.execute(new SendMessage(callbackQuery.from().id(), "Заказ устарел, оформите новый заказ"));
        }

        if (update.inlineQuery() != null) {
            this.execute(new AnswerInlineQuery(inlineQuery.id(), r1, r2));
        }
        if (message != null) {
            try {
                if (message.contact() != null) {
                    clientMap.put(message.chat().id(), new Client());
                    clientMap.get(message.chat().id()).setId(orderId);
                    clientMap.get(message.chat().id()).setPhone(message.contact().phoneNumber());
                    clientMap.get(message.chat().id()).setName(message.contact().firstName());
                    clientMap.get(message.chat().id()).setTgName(message.from().username());

                    deliveryMap.get(message.chat().id()).setSendPhone(false);
                    deliveryMap.get(message.chat().id()).setSendAdress(true);
                    this.execute(new SendMessage(message.chat().id(), "Для продолжения введите адрес. Пример: Kale mahalessi," +
                            " 110 sokak, apt.1a/7 (где 1а - номер дома, а 7 номер квартиры)"));
                    orderId++;
                }
                if (message.location() != null) {
                    if (cartMap.get(message.chat().id()) != null) {
                        List<Order> orderList = cartMap.get(message.chat().id());
                        StringBuilder str = new StringBuilder();
                        int count = 1;
                        int sum = 0;
                        String size = "small";
                        for (Order orders : orderList) {
                            if (orders.getPizza().isBig()) {
                                size = "BIG";
                            }
                            str.append(count).append(". ").append(orders.getPizza().getName()).append(" (").append(size).append(") * ").append(orders.getAmount()).
                                    append(" = ").append(orders.getOrderPrice()).append("\n");
                            size = "small";
                            count++;
                            sum += orders.getOrderPrice();
                        }
                        clientMap.get(message.chat().id()).setGeo(message.location().toString());
                        deliveryMap.get(message.chat().id()).setSendPhone(false);
                        deliveryMap.get(message.chat().id()).setSendAdress(false);
                        deliveryMap.get(message.chat().id()).setWaitConfirm(true);
                        this.execute(new SendMessage(message.from().id(), "Ваш заказ №" + clientMap.get(message.chat().id()).getId()
                                + " находится в обработке. Это займет не более 10 минут. Ожидайте подтверждение"));

                        this.execute(new SendLocation(521030602, message.location().latitude(), message.location().longitude()));

                        this.execute(orderMessage.createMessage(521030602, clientMap.get(message.chat().id()).getId(),
                                clientMap.get(message.chat().id()).getName(), clientMap.get(message.chat().id()).getTgName(),
                                clientMap.get(message.chat().id()).getPhone(), clientMap.get(message.chat().id()).getAddress(),
                                str, sum));

                        deliveryMap.get(message.chat().id()).setWaitConfirm(false);

                        saveOrder.save(clientMap.get(message.chat().id()).getId(), clientMap.get(message.chat().id()).getName(),
                                clientMap.get(message.chat().id()).getTgName(), clientMap.get(message.chat().id()).getPhone(),
                                clientMap.get(message.chat().id()).getAddress(), str, sum);

                        cartMap.get(message.chat().id()).clear();
                    }
                }
            } catch (NullPointerException e) {
                this.execute(new SendMessage(callbackQuery.from().id(), "Заказ устарел, оформите новый заказ"));
            }
            String text = message.text();
            Optional.ofNullable(text)
                    .ifPresent(commandName -> this.serveCommand(commandName, message.chat().id(), update));
        }
    }

    private void serveCommand(String commandName, Long chatId, Update update) {
        try {
            switch (commandName) {
                case "/start" -> {
                    this.execute(new SendPhoto(chatId, new File("../photo/photo_2022-12-08_00-49-38.jpg")).caption("\uD83C\uDF55 WhiteAngel пицца\n" +
                            "\uD83D\uDED2 Здравствуйте! Рады предложить Вам доставку пиццы и напитков.\n" +
                            "Мы доставим Ваш заказ в течении 60 минут в любую точку Финике.\n" +
                            "Оплата курьеру по факту доставки наличными или картой.").replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton("\uD83C\uDF55Меню").switchInlineQueryCurrentChat(""))));
                }
                case "newOrder" -> {
                    List<Pizza> pizzas = new ArrayList<>();
                    pizzas.add(p1);
                    pizzas.add(p2);
                    GetUpdatesResponse updatesResponse = this.execute(new GetUpdates());
                    List<Update> updates = updatesResponse.updates();
                    for (Update upd : updates) {
                        if (upd.chosenInlineResult() != null) {
                            for (Pizza piz : pizzas) {
                                if (upd.chosenInlineResult().resultId().equals(piz.getId())) {
                                    Order order = new Order(piz, piz.getPrice(), 1, 0, 0, chatId);
                                    orderMap.put(chatId, order);

                                    SendPhoto response = new SendPhoto(chatId, piz.getPhoto()).caption(piz.getName() + "\n" +
                                            piz.getDescription() + "\nЦена: " + piz.getPrice() + "₽").replyMarkup(kb.productKb());
                                    this.execute(response);
                                }
                            }
                        }
                    }
                }
                default -> {
                    if (deliveryMap.get(chatId) != null) {
                        if (deliveryMap.get(chatId).isSendPhone()) {
                            String number = commandName.replaceAll("[^\\d]", "");
                            if (number.length() < 11) {
                                this.execute(new SendMessage(chatId, "Пожалуйста, попробуйте ещё раз, или нажмите кнопку " +
                                        "«Отправить контакт»\uD83D\uDC47"));
                                break;
                            } else {
                                clientMap.put(chatId, new Client());
                                clientMap.get(chatId).setId(orderId);
                                clientMap.get(chatId).setPhone(commandName);
                                clientMap.get(chatId).setName(update.message().from().firstName());
                                clientMap.get(chatId).setTgName(update.message().from().username());

                                deliveryMap.get(chatId).setSendPhone(false);
                                deliveryMap.get(chatId).setSendAdress(true);
                                this.execute(new SendMessage(chatId, "Для продолжения введите адрес. Пример: Kale mahalessi," +
                                        " 110 sokak, apt.1a/7 (где 1а - номер дома, а 7 номер квартиры)"));
                                orderId++;
                                break;
                            }
                        }
                        if (deliveryMap.get(chatId).isSendAdress()) {
                            clientMap.get(chatId).setAddress(commandName);
                            this.execute(new SendMessage(chatId, "Хотите отправить геопозицию? Это поможет курьеру быстрее " +
                                    "Вас найти").replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton("Отправить геопозицию").
                                    requestLocation(true)).resizeKeyboard(true).addRow(new KeyboardButton("Не отправлять")).
                                    oneTimeKeyboard(true)));
                            deliveryMap.get(chatId).setSendPhone(false);
                            deliveryMap.get(chatId).setSendAdress(false);
                            deliveryMap.get(chatId).setWaitConfirm(true);
                            break;
                        }
                        if (deliveryMap.get(chatId).isWaitConfirm()) {
                            List<Order> orderList = cartMap.get(chatId);
                            StringBuilder str = new StringBuilder();
                            int count = 1;
                            int sum = 0;
                            String size = "small";
                            for (Order orders : orderList) {
                                if (orders.getPizza().isBig()) {
                                    size = "BIG";
                                }
                                str.append(count).append(". ").append(orders.getPizza().getName()).append(" (").append(size).append(") * ").append(orders.getAmount()).
                                        append(" = ").append(orders.getOrderPrice()).append("\n");
                                size = "small";
                                count++;
                                sum += orders.getOrderPrice();
                            }
                            this.execute(new SendMessage(chatId, "Ваш заказ находится в обработке. " +
                                    "Это займет не более 10 минут. Ожидайте подтверждение"));

                            this.execute(orderMessage.createMessage(521030602, clientMap.get(chatId).getId(),
                                    clientMap.get(chatId).getName(), clientMap.get(chatId).getTgName(),
                                    clientMap.get(chatId).getPhone(), clientMap.get(chatId).getAddress(),
                                    str, sum));

                            deliveryMap.get(chatId).setWaitConfirm(false);

                            saveOrder.save(clientMap.get(chatId).getId(), clientMap.get(chatId).getName(), clientMap.get(chatId).getTgName(),
                                    clientMap.get(chatId).getPhone(), clientMap.get(chatId).getAddress(), str, sum);

                            cartMap.get(chatId).clear();
                            break;
                        }
                    }
                    SendMessage response = new SendMessage(chatId, "Команда не найдена");
                    this.execute(response);
                }
            }
        } catch (NullPointerException e) {
            this.execute(new SendMessage(chatId, "Заказ устарел, оформите новый заказ"));
        }
    }
}

