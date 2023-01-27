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
        String getTextMessage = update.getMessage().getText();
        Long getChatIdUser = update.getMessage().getChatId();
        Long getIdUser = update.getMessage().getChat().getId();
        People people = peopleService.findById(getIdUser);
        if (statusInput) {
            if (people.getAge() == 0) {
                int age = Integer.parseInt(getTextMessage);
                people.setAge(age);
                sendMessage(getChatIdUser, "Введите ваше имя");
            } else if (people.getName().isEmpty()) {
                people.setName(getTextMessage);
                sendMessage(getChatIdUser, "Введите ваш город");
            } else if (people.getNameCity().isEmpty()) {
                people.setNameCity(getTextMessage);
                sendMessage(getChatIdUser, "Расскажи немного о себе");
            } else if (people.getBio().isEmpty()) {
                people.setBio(getTextMessage);
                statusInput = false;
                sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n" +
                        "2. Моя анкета");
                statusEditProfile = false;
            }
            peopleService.save(people);
        } else if (statusEditProfile) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                switch (getTextMessage) {
                    case "1":
                        people.setAge(0);
                        people.setName("");
                        people.setNameCity("");
                        people.setBio("");
                        peopleService.save(people);
                        sendMessage(getChatIdUser, "Сколько тебе лет?");
                        statusInput = true;
                        statusEditProfile = false;
                        break;
                    case "2":
                        sendMessageEdit(getChatIdUser, "Находится в разработке");
                        break;
                    case "3":
                        sendMessageForEdit(getChatIdUser, "Расскажи о себе, кого хочешь найти, чем предлагаешь заняться");
                        editBio = true;
                        statusEditProfile = false;
                        break;
                    case "4":
                        sendMessage(getChatIdUser, "\n" +
                                "Введите имя пользователя в Instagram");
                        statusInstagram = true;
                        statusEditProfile = false;
                        break;
                    case "5":
                        findPeople(getChatIdUser, "\uD83D\uDC4E");
                        statusEditProfile = false;
                        break;
                }
            }
        } else if (statusInstagram) {
            people.setNameInstagram(getTextMessage);
            peopleService.save(people);
            sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n" +
                    "2. Моя анкета");
            statusInstagram = false;
        } else if (editBio) {
            people.setBio(getTextMessage);
            peopleService.save(people);
            sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n" +
                    "2. Моя анкета");
            editBio = false;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            switch (getTextMessage) {
                case "Привет":
                    String message = EmojiParser.parseToUnicode("Привет " + update.getMessage().getChat().getFirstName() + " :blush:");
                    sendMessage(getChatIdUser, message);
                    break;
                case "/start":
                    String message2 = EmojiParser.parseToUnicode("Выбери язык :point_down:");
                    sendMessStart(getChatIdUser, message2);
                    break;
                case "\uD83C\uDDF7\uD83C\uDDFA Русский":
                    if (people != null) {
                        sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n" +
                                "2. Моя анкета");
                    } else {
                        String message3 = EmojiParser.parseToUnicode("Уже миллионы людей знакомятся в\n" +
                                "PolytechLove:heart_eyes:\n" +
                                "\nЯ помогу найте тебе пару или просто друзей");
                        sendMessageRu(getChatIdUser, message3);
                    }
                    break;
                case "\uD83D\uDC4C давай начнем":
                    statusInput = true;
                    people = new People();
                    people.setId(getIdUser);
                    people.setUser(update.getMessage().getFrom().getUserName());
                    people.setName("");
                    people.setNameCity("");
                    people.setBio("");
                    peopleService.save(people);
                    sendMessage(getChatIdUser, "Введите ваш возраст");
                    break;
                case "1":
                    findPeople(getChatIdUser, "\uD83D\uDC4E");
                    break;
                case "2":
                    sendMessageEdit(getChatIdUser, "Так выглядит твоя анкета:");
                    if (people.getNameInstagram() != null && !people.getNameInstagram().isEmpty()) {
                        sendMessageEdit(getChatIdUser, people.getName() + ", " + people.getAge() + ", " + people.getNameCity() + " - " + people.getBio() + "\ninst: " + people.getNameInstagram());
                    } else {
                        sendMessageEdit(getChatIdUser, people.getName() + ", " + people.getAge() + ", " + people.getNameCity() + " - " + people.getBio());
                    }
                    sendMessageEdit(getChatIdUser, "1. Заполнить анкету заново.\n" +
                            "2. Изменить фото/видео.\n" +
                            "3. Изменить текст анкеты.\n" +
                            "4. Привязка Instagram.\n" +
                            "5. Смотреть анкеты.");
                    statusEditProfile = true;
                    break;
                //TODO Лайк который ты ставишь
                case "❤":
                    findPeople(getChatIdUser, "❤");
                    break;
                case "\uD83D\uDC4E":
                    findPeople(getChatIdUser, "\uD83D\uDC4E");
                    break;
                case "\uD83D\uDE34":
                    sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n" +
                            "2. Моя анкета");
                    break;
                case "1 \uD83D\uDC4D":
                    likeForPeople(getChatIdUser, "1");
                    break;
                case "2 \uD83D\uDE34":
                    likeForPeople(getChatIdUser, "2");
                    break;
                case "1 ❤":
                    likeForPeopleBefore(getChatIdUser, "1");
                    break;
                case "3 \uD83D\uDC4E":
                    likeForPeopleBefore(getChatIdUser, "3");
                    break;
            }
        }
    }

    //TODO Для взаимных лайков надо сделать
    private void likeForPeople(Long chatId, String messages) {
        if (messages.equals("2")) {
            List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
            PeopleLike peopleLike = peopleLikes.get(0);
            peopleLikeService.removeByMeAndYou(peopleLike);
            return;
        }
        List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
        People people1 = peopleService.findById(peopleLikes.get(0).getMe());
        if (people1.getNameInstagram() == null || people1.getNameInstagram().isEmpty()) {
            sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio());
        } else {
            sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram());
        }
    }

    private void likeForPeopleBefore(Long chatId, String messages) {
        if (messages.equals("3")) {
            List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
            PeopleLike peopleLike = peopleLikes.get(0);
            peopleLikeService.removeByMeAndYou(peopleLike);
            return;
        } else if (messages.equals("1")) {
            List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
            PeopleLike peopleLike = peopleLikes.get(0);
            People me = peopleService.findById(peopleLike.getMe());
            People you = peopleService.findById(chatId);
            //TODO ЗДЕСЬ
            sendMessageLikeForPeopleBefore(chatId, "Отлично! Надеюсь хорошо проведете время \uD83D\uDE09 Начинай общаться \uD83D\uDC49 @" + me.getUser() + " \uD83D\uDC97");
            sendMessageLikeForPeopleBefore(peopleLike.getMe(), "Отлично! Надеюсь хорошо проведете время \uD83D\uDE09 Начинай общаться \uD83D\uDC49 @" + you.getUser() + " \uD83D\uDC97");
            peopleLikeService.removeByMeAndYou(peopleLike);
        }

    }

    private void sendMessageLikeForPeopleBefore(Long chatId, String messages) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messages);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1 ❤");
        row.add("3 \uD83D\uDC4E");
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

    private void findPeople(Long chatId, String message) {
        People peopleMain = peopleService.findById(chatId);
        if (message.equals("❤")) {
            People peopleYouLike = peopleService.findById(peopleMain.getIdLastAccountFind());
            peopleLikeService.save(new PeopleLike(chatId, peopleYouLike.getId()));
            //TODO Доработать скольким ты понравился
            sendMessageForLike(peopleYouLike.getId(), "Ты понравился " + " девушке, показать её?\n" +
                    "\n1. Показать.\n2. Не хочу больше никого смотреть.");
        }
        try {
            List<People> peopleList = peopleService.findAllByNameCityAndAgeBetweenAndIdNot(peopleMain.getNameCity(), peopleMain.getAge() - 3, peopleMain.getAge() + 2, chatId);
            int randomNumber = (int) (Math.random() * peopleList.size());
            People people2 = peopleList.get(randomNumber);
            if (people2.getNameInstagram() != null && !people2.getNameInstagram().isEmpty()) {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio() + "\ninst: " + people2.getNameInstagram());
            } else {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio());
            }
            peopleMain.setIdLastAccountFind(people2.getId());
            peopleService.save(peopleMain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageForLike(Long chatId, String textToSend) {
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
        People people = peopleService.findById(chatId);
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