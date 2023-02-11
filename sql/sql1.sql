CREATE TABLE people_like
(
    id_main_profile    BIGINT NOT NULL,
    id_people_for_like BIGINT NOT NULL,
    message            VARCHAR(255)
);

CREATE TABLE people
(
    id                  SERIAL,
    id_account          BIGINT  NOT NULL,
    user_name           VARCHAR(25),
    name                VARCHAR(25),
    age                 INTEGER,
    name_city           VARCHAR(25),
    bio                 VARCHAR(255),
    account_inst        VARCHAR(35),
    account_find        BIGINT,
    gender              VARCHAR(25),
    gender_find         VARCHAR(25),
    status_input        BOOLEAN NOT NULL,
    edit_bio            BOOLEAN NOT NULL,
    status_edit_profile BOOLEAN NOT NULL,
    status_instagram    BOOLEAN NOT NULL,
    message_like_status BOOLEAN NOT NULL,
    message_like        VARCHAR(255),
    img                 BYTEA,
    PRIMARY KEY (id)
);

INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (6546546, 'Петя', 16, 'Астана', 'Овер2', 'alken1t', 'Парень');
INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (654654765, 'Маша', 17, 'Астана', 'Симс', 'alken1t', 'Девушка');
INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (654665765, 'Дима', 18, 'Астана', 'Дока3', 'alken1t', 'Парень');
INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (654654765, 'Катя', 16, 'Астана', 'Симс', 'alken1t', 'Девушка');
INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (65462356, 'Генадий', 16, 'Нур-Султан', 'Овер2', 'alken1t', 'Парень');
INSERT INTO people (id, name, age, name_city, bio, account_inst, gender)
VALUES (65466543, 'Василий', 17, 'Нур-Султан', 'Овер2', 'alken1t', 'Парень');