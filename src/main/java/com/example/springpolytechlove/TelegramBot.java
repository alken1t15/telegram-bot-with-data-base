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

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private PeopleLikeService peopleLikeService;

    private boolean statusInput;

    private boolean editBio;

    private boolean statusEditProfile;

    private boolean statusInstagram;

    private boolean messageLikeStatus;
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
        People people = peopleService.findByIdAccount(getIdUser);
        if (people != null) {
            statusInput = people.getStatusInput();
            editBio = people.getEditBio();
            statusEditProfile = people.getStatusEditProfile();
            statusInstagram = people.getStatusInstagram();
            messageLikeStatus = people.getMessageLikeStatus();
        }
        if (statusInput) {
            if (people.getAge() == 0) {
                try {
                    int age = Integer.parseInt(getTextMessage);
                    if (age < 0 || age > 100) {
                        throw new NullPointerException();
                    }
                    people.setAge(age);
                    sendMessageEdit(getChatIdUser, "Введите ваше имя", people);
                } catch (NullPointerException e) {
                    sendMessageEdit(getChatIdUser, "Введите ваш возраст", people);
                }
            } else if (people.getName().isEmpty()) {
                people.setName(getTextMessage);
                sendMessageGender(getChatIdUser);
            } else if (people.getGender().isEmpty()) {
                if (getTextMessage.equals("Я парень")) {
                    people.setGender("Парень");
                } else if (getTextMessage.equals("Я девушка")) {
                    people.setGender("Девушка");
                } else {
                    sendMessageGender(getChatIdUser);
                }
                sendMessageEdit(getChatIdUser, "Введите ваш город", people);
            } else if (people.getNameCity().isEmpty()) {
                people.setNameCity(getTextMessage);
                sendMessageGenderFind(getChatIdUser);
            } else if (people.getGenderFind().isEmpty()) {
                switch (getTextMessage) {
                    case "Парней" -> people.setGenderFind("Парень");
                    case "Девушек" -> people.setGenderFind("Всех");
                    case "Всех" -> people.setGenderFind("Девушка");
                    default -> sendMessageGenderFind(getChatIdUser);
                }
                sendMessageEdit(getChatIdUser, "Расскажи немного о себе", people);
            } else if (people.getBio().isEmpty()) {
                people.setBio(getTextMessage);
                people.setStatusInput(false);
                sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n2. Моя анкета");
                people.setStatusEditProfile(false);
            }
        } else if (statusEditProfile) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                switch (getTextMessage) {
                    case "1" -> {
                        people.setAge(0);
                        people.setName("");
                        people.setNameCity("");
                        people.setBio("");
                        people.setGender("");
                        people.setGenderFind("");
                        sendMessage(getChatIdUser, "Сколько тебе лет?");
                        people.setStatusInput(true);
                        people.setStatusEditProfile(false);
                    }
                    case "2" -> sendMessageEdit(getChatIdUser, "Находится в разработке");
                    case "3" -> {
                        sendMessageForEdit(getChatIdUser);
                        people.setEditBio(true);
                        people.setStatusEditProfile(false);
                    }
                    case "4" -> {
                        sendMessage(getChatIdUser, "\n" +
                                "Введите имя пользователя в Instagram");
                        people.setStatusEditProfile(false);
                        people.setStatusInstagram(true);
                    }
                    case "5" -> {
                        findPeople(getChatIdUser, "\uD83D\uDC4E");
                        people.setStatusEditProfile(false);
                    }
                }
            }
        } else if (statusInstagram) {
            people.setNameInstagram(getTextMessage);
            sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n2. Моя анкета");
            people.setStatusInstagram(false);
        } else if (editBio) {
            people.setBio(getTextMessage);
            sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n2. Моя анкета");
            people.setEditBio(false);
        } else if (messageLikeStatus) {
            people.setMessageLike(getTextMessage);
            findPeople(getChatIdUser, "\uD83D\uDC8C");
            people.setMessageLikeStatus(false);
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
                        sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n2. Моя анкета");
                    } else {
                        String message3 = EmojiParser.parseToUnicode("Уже миллионы людей знакомятся в\n" +
                                "PolytechLove:heart_eyes:\n" +
                                "\nЯ помогу найте тебе пару или просто друзей");
                        sendMessageRu(getChatIdUser, message3);
                    }
                    break;
                case "\uD83D\uDC4C давай начнем":
                    people = new People();
                    people.setIdAccount(getIdUser);
                    people.setUser(update.getMessage().getFrom().getUserName());
                    people.setName("");
                    people.setNameCity("");
                    people.setBio("");
                    people.setStatusInput(true);
                    people.setEditBio(false);
                    people.setStatusEditProfile(false);
                    people.setStatusInstagram(false);
                    people.setMessageLikeStatus(false);
                    people.setAge(0);
                    sendMessageEdit(getChatIdUser, "Введите ваш возраст", people);
                    break;
                case "1":
                    people = findPeople(getChatIdUser, "\uD83D\uDC4E");
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
                    people.setStatusEditProfile(true);
                    break;
                //TODO Лайк который ты ставишь
                case "❤️":
                    people = findPeople(getChatIdUser, "❤️");
                    break;
                case "\uD83D\uDC4E":
                    people = findPeople(getChatIdUser, "\uD83D\uDC4E");
                    break;
                case "\uD83D\uDC8C":
                    people.setMessageLikeStatus(true);
                    sendMessage(getChatIdUser, "Введите сообщение которые вы хотите отправить");
                    break;
                case "\uD83D\uDE34":
                    sendMainMessage(getChatIdUser, "1. Смотреть анкеты\n2. Моя анкета");
                    break;
                case "1 \uD83D\uDC4D":
                    likeForPeople(getChatIdUser, "1");
                    break;
                case "2 \uD83D\uDE34":
                    likeForPeople(getChatIdUser, "2");
                    break;
                case "1 ❤️":
                    likeForPeopleBefore(getChatIdUser, "1");
                    break;
                case "3 \uD83D\uDC4E":
                    likeForPeopleBefore(getChatIdUser, "3");
                    break;
                default: {
                    sendMessage(getChatIdUser, "Такой команды нету!");
                    System.out.println(getTextMessage);
                }
            }
        }
        if (people != null) {
            peopleService.saved(people);
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
        People people1 = peopleService.findByIdAccount(peopleLikes.get(0).getMe());
        if (peopleLikes.get(0).getMessage() == null) {
            if (people1.getNameInstagram() == null || people1.getNameInstagram().isEmpty()) {
                sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio());
            } else {
                sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram());
            }
        } else {
            if (people1.getNameInstagram() == null || people1.getNameInstagram().isEmpty()) {
                sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\nСообщение для тебя\uD83D\uDC8C: " + peopleLikes.get(0).getMessage());
            } else {
                sendMessageLikeForPeopleBefore(chatId, people1.getName() + ", " + people1.getAge() + ", " + people1.getNameCity() + " - " + people1.getBio() + "\ninst: " + people1.getNameInstagram() + "\nСообщение для тебя\uD83D\uDC8C: " + peopleLikes.get(0).getMessage());
            }
        }
    }

    private void likeForPeopleBefore(Long chatId, String messages) {
        if (messages.equals("3")) {
            List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
            PeopleLike peopleLike = peopleLikes.get(0);
            peopleLikeService.removeByMeAndYou(peopleLike);
        } else if (messages.equals("1")) {
            List<PeopleLike> peopleLikes = peopleLikeService.findByYou(chatId);
            PeopleLike peopleLike = peopleLikes.get(0);
            People me = peopleService.findByIdAccount(peopleLike.getMe());
            People you = peopleService.findByIdAccount(chatId);
            //TODO ЗДЕСЬ
            sendMainMessage(chatId, "Отлично! Надеюсь хорошо проведете время \uD83D\uDE09 Начинай общаться \uD83D\uDC49 @" + me.getUser() + " \uD83D\uDC97" + "\n\"1. Смотреть анкеты\n2. Моя анкета\"");
            sendMainMessage(peopleLike.getMe(), "Отлично! Надеюсь хорошо проведете время \uD83D\uDE09 Начинай общаться \uD83D\uDC49 @" + you.getUser() + " \uD83D\uDC97" + "\n\"1. Смотреть анкеты\n2. Моя анкета\"");
            peopleLikeService.removeByMeAndYou(peopleLike);
        }

    }

    private People findPeople(Long chatId, String message) {
        People peopleMain = peopleService.findByIdAccount(chatId);
        if (message.equals("❤️")) {
            People peopleYouLike = peopleService.findByIdAccount(peopleMain.getAccountFind());
            peopleLikeService.save(new PeopleLike(chatId, peopleYouLike.getIdAccount()));
            //TODO Доработать скольким ты понравился
            sendMessageForLike(peopleYouLike.getIdAccount());
        } else if (message.equals("\uD83D\uDC8C")) {
            People peopleYouLike = peopleService.findByIdAccount(peopleMain.getAccountFind());
            peopleLikeService.save(new PeopleLike(chatId, peopleYouLike.getIdAccount(), peopleMain.getMessageLike()));
            //TODO Доработать скольким ты понравился
            sendMessageForLike(peopleYouLike.getIdAccount());
        }
        try {
            List<People> peopleList;
            if (peopleMain.getGenderFind().equals("Парень") || peopleMain.getGenderFind().equals("Девушка")) {
                peopleList = peopleService.findAllByNameCityAndGenderAndAgeBetweenAndIdAccountNot(peopleMain.getNameCity(), peopleMain.getGender(), peopleMain.getAge() - 3, peopleMain.getAge() + 2, chatId);
            } else {
                peopleList = peopleService.findAllByNameCityAndAgeBetweenAndIdAccountNot(peopleMain.getNameCity(), peopleMain.getAge() - 3, peopleMain.getAge() + 2, chatId);
            }
            int randomNumber = (int) (Math.random() * peopleList.size());
            People people2 = peopleList.get(randomNumber);
            if (people2.getNameInstagram() != null && !people2.getNameInstagram().isEmpty()) {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio() + "\ninst: " + people2.getNameInstagram());
            } else {
                sendMessageFind(chatId, people2.getName() + ", " + people2.getAge() + ", " + people2.getNameCity() + " - " + people2.getBio());
            }
            peopleMain.setAccountFind(people2.getIdAccount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return peopleMain;
    }

    //TODO ПЕРЕДЕЛАТЬ ЛОГИКУ ДЛЯ ЗАНОВО ЗАПОЛНЕНИЯ ПРОФИЛЯ
    private void sendMessageEdit(Long chatId, String text, People people) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        if (!people.getName().isEmpty() && text.equals("Введите ваше имя")) {

            row.add(people.getName());
        } else if (people.getAge() != 0 && text.equals("Введите ваше имя")) {

            row.add(String.valueOf(people.getAge()));
        } else if (!people.getNameCity().isEmpty() && text.equals("Введите ваше имя")) {

            row.add(people.getNameCity());
        } else if (!people.getBio().isEmpty() && text.equals("Расскажи немного о себе")) {

            row.add(people.getBio());
        }
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

    private void sendMessageGender(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Кто вы?");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("Я парень");
        row.add("Я девушка");
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

    private void sendMessageGenderFind(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Введите кого ищите");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();


        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("Парней");
        row.add("Девушек");
        row.add("Всех");
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

    private void sendMessageLikeForPeopleBefore(Long chatId, String messages) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messages);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("1 ❤️");
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

    private void sendMessageForLike(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Ты понравился  девушке, показать её?\n\n1. Показать.\n2. Не хочу больше никого смотреть.");
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

        row.add("❤️");
        row.add("\uD83D\uDC4E");
        row.add(EmojiParser.parseToUnicode(":love_letter:"));
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

    private void sendMainMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
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

    private void sendMessageForEdit(long chatId) {
        People people = peopleService.findByIdAccount(chatId);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Расскажи о себе, кого хочешь найти, чем предлагаешь заняться");
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