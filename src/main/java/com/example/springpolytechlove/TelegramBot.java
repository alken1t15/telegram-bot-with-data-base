package com.example.springpolytechlove;

import com.example.springpolytechlove.config.BotConfig;
import com.example.springpolytechlove.model.People;
import com.example.springpolytechlove.model.PeopleService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private PeopleService peopleService;

    private People people;
    private boolean statusInput =false;
    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(statusInput){
            if(people.getAge() == 0){
                int age = Integer.parseInt(update.getMessage().getText());
                people.setAge(age);
                sendMessage(update.getMessage().getChatId(),"Введите ваше имя");
            }
            else if(people.getName()==null || people.getName().isEmpty()){
                String name = update.getMessage().getText();
                people.setName(name);
                sendMessage(update.getMessage().getChatId(),"Введите ваш город");
            }
            else if(people.getNameCity()==null || people.getNameCity().isEmpty()){
                String city = update.getMessage().getText();
                people.setNameCity(city);
                sendMessage(update.getMessage().getChatId(),"Расскажи немного о себе");
            }
            else if(people.getBio()==null || people.getBio().isEmpty()){
                String bio = update.getMessage().getText();
                people.setBio(bio);
                System.out.println(people);
                peopleService.save(people);
                statusInput= false;
                sendMainPage(update.getMessage().getChatId(),"1. Смотреть анкеты\n" +
                        "2. Моя анкета");
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
           // People people = peopleService.findById(update.getMessage().getChat().getId()).get(0);
            switch (messageText){
                case "Привет":
                    System.out.println(update);
                    String message =  EmojiParser.parseToUnicode("Привет " + update.getMessage().getChat().getFirstName() + " :blush:");
                    sendMessage(update.getMessage().getChatId(),message);
                    break;
                case "/start":
                    String message2 =  EmojiParser.parseToUnicode("Выбери язык :point_down:");
                    sendMessStart(update.getMessage().getChatId(),message2);
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA Русский":
                    try {
                        People people1 = peopleService.findById(update.getMessage().getChat().getId()).get(0);
                        sendMainPage(update.getMessage().getChatId(),"1. Смотреть анкеты\n" +
                                "2. Моя анкета");
                    }catch (Exception e) {
                        String message3 = EmojiParser.parseToUnicode("Уже миллионы людей знакомятся в\n" +
                                "PolytechLove:heart_eyes:\n" +
                                "\nЯ помогу найте тебе пару или просто друзей");
                        sendMessageRu(update.getMessage().getChatId(), message3);
                    }
                    break;
                case "\uD83D\uDC4C давай начнем":
                    statusInput = true;
                    people = new People();
                    people.setId(update.getMessage().getChat().getId());
                    sendMessage(update.getMessage().getChatId(),"Введите ваш возраст");
                    break;
                case "2":
                    People people1 = peopleService.findById(update.getMessage().getChat().getId()).get(0);
                    sendMainPage(update.getMessage().getChatId(),people1.getName()+", "+people1.getAge()+", "+people1.getNameCity()+" - "+people1.getBio());
                    break;
            }
        }
    }

    private void sendMainPage(Long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1");
        row.add("2");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageRu(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("\uD83D\uDC4C давай начнем");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessStart(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("\uD83C\uDDF7\uD83C\uDDFA Русский");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
