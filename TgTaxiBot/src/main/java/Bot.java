import entity.OptionEntity;
import entity.TaxiInfo;
import org.glassfish.jersey.server.ManagedAsync;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {

    private boolean inputAdress = false;
    private boolean inputPrice = false;

    @Override
    public String getBotUsername() {
        return "@TlgrmTaxiBot";
    }

    @Override
    public String getBotToken() { return "1992299744:AAHCse1Tv-QAEyTi39KJzBn1_WoGzK28KXE"; }

    @Override
    public synchronized void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallBack(update.getCallbackQuery().getMessage(), update.getCallbackQuery());
        } else if (update.getMessage().getText().equals("/start")) {
            buttonsList(update.getMessage());
        } else if (update.hasMessage() & inputAdress) {
            Taxi taxi = new Taxi();
            String[] adressPoints = update.getMessage().getText().split("-");
            taxi.setBeginAdress(adressPoints[0]);
            taxi.setEndAdress(adressPoints[1]);
            try {
                TaxiInfo taxiInfo = taxi.taxiInfo();
                List<String> prices = taxiInfo.getOptions()
                        .stream()
                        .map(OptionEntity::getPrice)
                        .collect(Collectors.toList());
                if (taxiInfo.getTimeText() != null) {
                    write(update.getMessage(), "На данных момент стоимость такси составляет ~" + String.join(" или ", prices) + " руб."
                            + " \nПриблизительное время поездки " + taxiInfo.getTimeText());
                } else {
                    write(update.getMessage(), "На данный момент стоимость такси составляет ~" + String.join(" или ", prices) + " руб."
                            + "Вы указали одинаковые адреса отправления и назначения");
                }
                buttonsList(update.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputAdress = false;
        } else if (update.hasMessage() & inputPrice) {
            checkPrice(update.getMessage(), update);
            inputPrice = false;
        } else if (update.hasMessage() | update.hasCallbackQuery()) {
            System.out.println(update.getMessage().getText());
            write(update.getMessage(), "Нет такой команды\n" +
                    "Для вызова меню отправьте /start");
        }
    }

    public void checkPrice (Message msg, Update update) {
        Thread myThread = new Thread(new Runnable() {
            Message message = msg;
            @ManagedAsync
            @Override
            public void run() {
                boolean checkPrice = true;
                int timeCounter = 0;
                int priceCheckCounter = 0;
                write(message, "Отслеживание стоимости запущено" +
                        "\nЖдите сообщения о снижении стоимости");
                while (checkPrice) {
                    try {
                        message = update.getMessage();
                        Taxi taxi = new Taxi();
                        timeCounter++;
                        String[] adressPoints = message.getText().split("-");
                        taxi.setBeginAdress(adressPoints[0]);
                        taxi.setEndAdress(adressPoints[1]);
                        TaxiInfo taxiInfo = taxi.taxiInfo();
                        List<String> prices = taxiInfo.getOptions()
                                .stream()
                                .map(OptionEntity::getPrice)
                                .collect(Collectors.toList());
                        String price = String.join(" или ", prices);
                        System.out.println("желаемая цена " + Double.parseDouble(adressPoints[2]));
                        System.out.println("внатури цена " + Double.parseDouble(price));
                        if (update.getMessage().getText().equals("/stop")) {
                            checkPrice = false;
                            write(update.getMessage(), "Отслеживание стоимости остановлено");
                        }
                         if (Double.parseDouble(price) <= Double.parseDouble(adressPoints[2])) {
                            priceCheckCounter++;
                        } else {
                             priceCheckCounter = 0;
                             TimeUnit.SECONDS.sleep(7);
                         }
                        if (priceCheckCounter > 1) {
                            checkPrice = false;
                            System.out.println("______ОН УЕХАЛ_____");
                            write(message, "Позравляю!\n Стоимость такси сейчас " + price + " руб.");
                            write(message, "Для вызова такси перейдите по сформированной ссылке:");
                            write(message, "https://3.redirect.appmetrica.yandex.com/route?" +
                                    taxi.reference() +
                                    "&level=50" +
                                    "&ref=TlgrmTaxiBot" +
                                    "&appmetrica_tracking_id=25395763362139037");
                        }
                        if (timeCounter > 150) {
                            checkPrice = false;
                            write (message, "За 30 минут стоимость так и не снизилась, отслеживание остановлно." +
                                    "\nЕсли еще необходимо уехать, запустите заного");
                            buttonsList(message);
                        }
                        TimeUnit.SECONDS.sleep(3);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                inputPrice = false;
            }
        });
        myThread.start();
    }

    private synchronized void handleCallBack(Message message, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        switch(data) {
            case "Информация о такси":
                write(message,"Введите адресса отправления и назначения\n" +
                        "По форме (адрес 1 - адрес 2)");
                inputAdress = true;
                break;
            case "Задать желаемую стоимость поездки":
                write(message, "Введите адресса отправления и назначения, а также желаемую стоимость поездки\n " +
                        "по форме (адрес 1 - адрес 2 - стоимость)");
                inputPrice = true;
                break;
            default:
                break;
        }
    }

    public void write (Message message, String txt) {
        try {
            execute(SendMessage.builder().chatId(message.getChatId().toString()).text(txt).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void buttonsList (Message message) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(InlineKeyboardButton.builder().text("Информация о такси").callbackData("Информация о такси").build()));
        buttons.add(List.of(InlineKeyboardButton.builder().text("Задать желаемую стоимость поездки").callbackData("Задать желаемую стоимость поездки").build()));
        try {
            execute(SendMessage.builder().text("Выберите действие").chatId(message.getChatId().toString()).replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
