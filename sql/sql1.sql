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

INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (6546546, '����', 16, '������', '����2', 'alken1t', null,'������');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654654765, '����', 17, '������', '����', 'alken1t', null,'�������');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654665765, '����', 18, '������', '����3', 'alken1t', null,'������');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (654654765, '����', 16, '������', '����', 'alken1t', null,'�������');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (65462356, '�������', 16, '���-������', '����2', 'alken1t', null,'������');
INSERT INTO public.people (id, name, age, name_city, bio, account_inst, id_last_account_find,gender) VALUES (65466543, '�������', 17, '���-������', '����2', 'alken1t', null,'������');
