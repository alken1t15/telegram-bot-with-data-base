package com.example.springpolytechlove;

import com.example.springpolytechlove.config.BotConfig;
import com.example.springpolytechlove.model.People;
import com.example.springpolytechlove.model.PeopleService;
import com.example.springpolytechlove.model.modelpeoplelike.PeopleLike;
import com.example.springpolytechlove.model.modelpeoplelike.PeopleLikeService;
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
import java.util.Random;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private PeopleLikeService peopleLikeService;

    private People people;
    private boolean statusInput = false;

    private boolean editBio = false;

    private boolean statusEditProfile = false;

    private boolean statusInstagram = false;
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
        if (statusInput) {
            if (people.getAge() == 0) {
                int age = Integer.parseInt(update.getMessage().getText());
                people.setAge(age);
                sendMessage(update.getMessage().getChatId(), "Введите ваше имя");
            } else if (people.getName() == null || people.getName().isEmpty()) {
                String name = update.getMessage().getText();
                people.setName(name);
                sendMessage(update.getMessage().getChatId(), "Введите ваш город");
            } else if (people.getNameCity() == null || people.getNameCity().isEmpty()) {
                String city = update.getMessage().getText();
                people.setNameCity(city);
                sendMessage(update.getMessage().getChatId(), "Расскажи немного о себе");
            } else if (people.getBio() == null || people.getBio().isEmpty()) {
                String bio = update.getMessage().getText();
                people.setBio(bio);
                System.out.println(people);
                peopleService.save(people);
                statusInput = false;
                sendMainMessage(update.getMessage().getChatId(), "1. Смотреть анкеты\n" +
                        "2. Моя анкета");
                statusEditProfile = false;
            }
        } else if (statusEditProfile) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                switch (messageText) {
                    case "1":
                        People people1 = peopleService.findById(update.getMessage().getChat().getId());
                        people = people1;
                        people.setAge(0);
                        people.setName(null);
                        people.setNameCity(null);
                        people.setBio(null);
                        sendMessage(update.getMessage().getChatId(), "Сколько тебе лет?");
                        statusInput = true;
                        statusEditProfile = false;
                        break;
                    case "2":
                        sendMessageEdit(update.getMessage().getChatId(), "Находится в разработке");
                        break;
                    case "3":
                        People people2 = peopleService.findById(update.getMessage().getChat().getId());
                        people = people2;
                        sendMessageForEdit(update.getMessage().getChatId(), "Расскажи о себе, кого хочешь найти, чем предлагаешь заняться");
                        editBio = true;
                        statusEditProfile = false;
                        break;
                    case "4":
                        People people3 = peopleService.findById(update.getMessage().getChat().getId());
                        people = people3;
                        sendMessage(update.getMessage().getChatId(), "\n" +
                                "Введите имя пользователя в Instagram");
                        statusInstagram = true;
                        statusEditProfile = false;
                        break;
                    case "5":
                        findPeople(update.getMessage().getChatId(), update.getMessage().getChat().getId(),"\uD83D\uDC4E");
                        statusEditProfile = false;
                        break;
                }
            }
        } else if (statusInstagram) {
            String messageText = update.getMessage().getText();
            people.setNameInstagram(messageText);
            peopleService.save(people);
            sendMainMessage(update.getMessage().getChatId(), "1. Смотреть анкеты\n" +
                    "2. Моя анкета");
        } else if (editBio) {
            String messageText = update.getMessage().getText();
            people.setBio(messageText);
            peopleService.save(people);
            sendMainMessage(update.getMessage().getChatId(), "1. Смотреть анкеты\n" +
                    "2. Моя анкета");
            editBio = false;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            // People people = peopleService.findById(update.getMessage().getChat().getId()).get(0);
            People people1 = peopleService.findById(update.getMessage().getChat().getId());
            switch (messageText) {
                case "Привет":
                    System.out.println(update);
                    String message = EmojiParser.parseToUnicode("Привет " + update.getMessage().getChat().getFirstName() + " :blush:");
                    sendMessage(update.getMessage().getChatId(), message);
                    break;
                case "/start":
                    String message2 = EmojiParser.parseToUnicode("Выбери язык :point_down:");
                    sendMessStart(update.getMessage().getChatId(), message2);
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA Русский":
                        if(people1!= null) {
                            sendMainMessage(update.getMessage().getChatId(), "1. Смотреть анкеты\n" +
                                    "2. Моя анкета");
                        }
                        else {
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
                    sendMessage(update.getMessage().getChatId(), "Введите ваш возраст");
                    break;
                case "1":
                    findPeople(update.getMessage().getChatId(), update.getMessage().getChat().getId(),"\uD83D\uDC4E");
                    break;
                case "2":
                    sendMessageEdit(update.getMessage().getChatId(), "Так выглядит твоя анкета:");
                    if (people1.getNameInstagram() != null && !people1.getNameInstagram().isEmpty()) {
                        sendMessageEdit(update.getMessage().getChatId(), people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram());
                    } else {
                        sendMessageEdit(update.getMessage().getChatId(), people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio());
                    }
                    sendMessageEdit(update.getMessage().getChatId(), "1. Заполнить анкету заново.\n" +
                            "2. Изменить фото/видео.\n" +
                            "3. Изменить текст анкеты.\n" +
                            "4. Привязка Instagram.\n" +
                            "5. Смотреть анкеты.");
                    statusEditProfile = true;
                    break;
                case "❤":
                    //TODO Лайк
                    sendMessage(update.getMessage().getChatId(), "Находится в разработке");
                    findPeople(update.getMessage().getChatId(), update.getMessage().getChat().getId(),"❤");
                    break;
                case "\uD83D\uDC4E":
                    findPeople(update.getMessage().getChatId(), update.getMessage().getChat().getId(),"\uD83D\uDC4E");
                    break;
                case "\uD83D\uDE34":
                    sendMainMessage(update.getMessage().getChatId(), "1. Смотреть анкеты\n" +
                            "2. Моя анкета");
                    break;
                case "1 \uD83D\uDC4D":
                    likeForPeople(update.getMessage().getChat().getId(),"1",update.getMessage().getChatId());
                    if (people1.getNameInstagram() != null && !people1.getNameInstagram().isEmpty()) {
                        sendMessageEdit(update.getMessage().getChatId(), people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram());
                    } else {
                        sendMessageEdit(update.getMessage().getChatId(), people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio());
                    }
                    break;
                case "2 \uD83D\uDE34":
                    likeForPeople(update.getMessage().getChat().getId(),"2",update.getMessage().getChatId());
            }
        }
    }

    //TODO Для взаимных лайков надо сделать
    private void likeForPeople(Long id,String messages,Long chatId){
        if(messages.equals("2")){
            List<PeopleLike> peopleLikes = peopleLikeService.findByMainPeople(id);
            PeopleLike peopleLike = peopleLikes.get(0);
            peopleLikeService.removeByMainPeopleAndLike(peopleLike.getMainPeople(),peopleLike.getLike());
        }
        List<PeopleLike> peopleLikes = peopleLikeService.findByMainPeople(id);
        People people1 = peopleService.findById(peopleLikes.get(0).getMainPeople());
        if (people1.getNameInstagram() != null && !people1.getNameInstagram().isEmpty()) {
            sendMessageForLike(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram());
        } else {
            sendMessageForLike(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio());
        }
    }

    private void findPeople(Long chatId, long id,String message) {
        if(message.equals("❤")){
            peopleLikeService.save(new PeopleLike(people.getId(),id));
            People people1 = peopleService.findById(people.getId());
            sendMessageForLike(people.getId(),"Ты понравился "+ people1.getPeopleLike().size()+ " девушке, показать её?\n" +
                    "\n1. Показать.\n2. Не хочу больше никого смотреть.");
        }
        People people1 = peopleService.findById(id);
        try {
            List<People> peopleList = peopleService.findAllByNameCityAndAgeBetweenAndIdNot(people1.getNameCity(), people1.getAge() - 3, people1.getAge() + 2, id);
            int randomNumber = (int) (Math.random() * peopleList.size());
            People people2 = peopleList.get(randomNumber);
            people = people2;
            if (people2.getNameInstagram() != null && !people2.getNameInstagram().isEmpty()) {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio() + "\ninst: " + people2.getNameInstagram());
            } else {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageForLike(Long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1 \uD83D\uDC4D");
        row.add("2 \uD83D\uDE34");
        replyKeyboardMarkup.setResizeKeyboard(true);

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageFind(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("❤");
        row.add("\uD83D\uDC4E");
        row.add("\uD83D\uDE34");
        replyKeyboardMarkup.setResizeKeyboard(true);

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageEdit(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1");
        row.add("2");
        row.add("3");
        row.add("4");
        row.add("5");

        replyKeyboardMarkup.setResizeKeyboard(true);

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMainMessage(Long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1");
        row.add("2");

        keyboardRows.add(row);
        keyboardMarkup.setResizeKeyboard(true);

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

        keyboardMarkup.setResizeKeyboard(true);

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

    private void sendMessStart(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("\uD83C\uDDF7\uD83C\uDDFA Русский");

        keyboardRows.add(row);

        keyboardMarkup.setResizeKeyboard(true);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageForEdit(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add(people.getBio());

        replyKeyboardMarkup.setResizeKeyboard(true);

        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(replyKeyboardMarkup);
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