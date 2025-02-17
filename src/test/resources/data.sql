INSERT INTO users (email, login, name, birthday)
VALUES ('ivanov@yandex.ru', 'Ivan', 'Иван Иванов', '1988-05-11'),
       ('petrov@yandex.ru', 'Peter', 'Петр Петров', '1990-07-15'),
       ('sevenov@yandex.ru', 'Semen', 'Семен Семенов', '1992-03-20'),
       ('egorov4@yandex.ru', 'Egor', 'Егор Егоров', '1977-07-17');

INSERT INTO friendship (user_id, friend_id, status)
VALUES (1, 2, 'CONFIRMED'), (1, 3, 'UNCONFIRMED'), (3, 2, 'CONFIRMED');

INSERT INTO films (film_name, description, release_date, duration, mpa_id)
VALUES ('Фильм 1', 'Описание для фильма 1', '1999-09-19', 99, 1),
	('Фильм 2', 'Описание для фильма 2', '2022-02-22', 222, 2),
	('Фильм 3', 'Описание для фильма 3', '2013-03-13', 133, 3),
	('Фильм 4', 'Описание для фильма 4', '2004-04-04', 144, 4),
	('Фильм 5', 'Описание для фильма 5', '2005-05-05', 155, 5);

INSERT INTO films_genres (film_id, genre_id)
VALUES (1, 2), (1, 3), (2, 4), (3, 6), (4, 1), (4, 3), (5, 5);

INSERT INTO likes (film_id, user_id)
VALUES (1, 2), (1, 3), (1, 1);