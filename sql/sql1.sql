create table people_like
(
    id_main_profile    bigint not null,
    id_people_for_like bigint not null
);

create table people
(
    id                   bigint not null,
    user_name varchar(25),
    name                 varchar(25),
    age                  integer,
    name_city            varchar(25),
    bio                  varchar(255),
    account_inst         varchar(35),
    id_last_account_find bigint,
    gender               varchar(25),
    gender_find           varchar(25),
    status_input boolean not null,
    edit_bio boolean not null,
    status_edit_profile boolean not null,
    status_instagram boolean not null,
    primary key (id)
);

INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (6546546, 'Петя', 16, 'Астана', 'Овер2', 'alken1t', null,'Парень');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654654765, 'Маша', 17, 'Астана', 'Симс', 'alken1t', null,'Девушка');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654665765, 'Дима', 18, 'Астана', 'Дока3', 'alken1t', null,'Парень');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654654765, 'Катя', 16, 'Астана', 'Симс', 'alken1t', null,'Девушка');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (65462356, 'Генадий', 16, 'Нур-Султан', 'Овер2', 'alken1t', null,'Парень');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (65466543, 'Василий', 17, 'Нур-Султан', 'Овер2', 'alken1t', null,'Парень');
