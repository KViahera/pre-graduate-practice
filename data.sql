DROP TABLE IF EXISTS contest_problems CASCADE;
DROP TABLE IF EXISTS test_cases CASCADE;
DROP TABLE IF EXISTS problems CASCADE;
DROP TABLE IF EXISTS contests CASCADE;

CREATE TABLE problems (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    statement TEXT NOT NULL,
    input_format TEXT NOT NULL,
    output_format TEXT NOT NULL,
    time_limit_milliseconds INTEGER NOT NULL,
    memory_limit_megabytes INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE test_cases (
    id SERIAL PRIMARY KEY,
    input_data TEXT NOT NULL,               
    output_data TEXT NOT NULL,          
    is_sample BOOLEAN NOT NULL DEFAULT FALSE, 
    problem_id INT NOT NULL REFERENCES problems(id) ON DELETE CASCADE
);

CREATE TABLE contests (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INTEGER NOT NULL
);

CREATE TABLE contest_problems (
    id SERIAL PRIMARY KEY,
    problem_index INTEGER NOT NULL CHECK (problem_index > -1 AND problem_index < 26), 
    contest_id INT NOT NULL REFERENCES contests(id) ON DELETE CASCADE,
    problem_id INT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    UNIQUE (contest_id, problem_index),
    UNIQUE (contest_id, problem_id)
);

INSERT INTO contests (title, start_time, duration_minutes) VALUES
    ('Вводный тур: Основы синтаксиса', '2026-03-15 10:00:00+03', 120),
    ('Квалификация: Динамическое программирование', '2026-03-20 14:00:00+03', 180),
    ('Алгоритмы на графах: Малый кубок', '2026-03-25 12:00:00+03', 300),
    ('Тренировка перед финалом #1', '2026-04-01 09:00:00+03', 240),
    ('Математический интенсив', '2026-04-05 15:30:00+03', 150),
    ('Школьный этап: Округ А', '2026-04-10 10:00:00+03', 180),
    ('Школьный этап: Округ Б', '2026-04-10 10:00:00+03', 180),
    ('Турнир молодых талантов', '2026-04-15 11:00:00+03', 300),
    ('Битва структур данных', '2026-04-20 18:00:00+03', 120),
    ('Code Sprint: Weekend Edition', '2026-04-25 12:00:00+03', 60),
    ('Региональный отбор: День 1', '2026-05-01 10:00:00+03', 300),
    ('Региональный отбор: День 2', '2026-05-02 10:00:00+03', 300),
    ('Летние сборы: Открытый контест', '2026-06-15 09:00:00+03', 600),
    ('Ночной кодинг: Рекурсия', '2026-06-20 22:00:00+03', 180),
    ('Финальный этап вузовской олимпиады', '2026-07-01 10:00:00+03', 300);

INSERT INTO problems (title, statement, input_format, output_format, time_limit_milliseconds, memory_limit_megabytes)
VALUES (
    'Арбуз', 
    'Петя и Гена купили арбуз весом $w$ килограмм. Они хотят разделить его на две части так, чтобы каждая часть весила четное количество килограмм. При этом не обязательно, чтобы части были равны.',
    'Вводится одно целое число $w$ ($1 \le w \le 100$) — вес арбуза.',
    'Выведите YES, если арбуз можно разделить на две части с четным весом, и NO в противном случае.',
    1000, 256
);

INSERT INTO test_cases (input_data, output_data, is_sample, problem_id) VALUES 
    ('8', 'YES', TRUE, (SELECT id FROM problems WHERE title = 'Арбуз')),
    ('2', 'NO', TRUE, (SELECT id FROM problems WHERE title = 'Арбуз')),
    ('5', 'NO', TRUE, (SELECT id FROM problems WHERE title = 'Арбуз')),
    ('100', 'YES', FALSE, (SELECT id FROM problems WHERE title = 'Арбуз'));

INSERT INTO problems (title, statement, input_format, output_format, time_limit_milliseconds, memory_limit_megabytes)
VALUES (
    'Слишком длинные слова', 
    'Иногда слова кажутся слишком длинными. Слово считается слишком длинным, если его длина строго больше 10 символов. Такие слова заменяются на аббревиатуру: первая буква + количество букв между первой и последней + последняя буква.',
    'Первая строка содержит целое число $n$ ($1 \le n \le 100$). Далее следуют $n$ строк со словами.',
    'Для каждого слова выведите его аббревиатуру или само слово, если оно не слишком длинное.',
    2000, 256
);

INSERT INTO test_cases (input_data, output_data, is_sample, problem_id) VALUES 
('4\nword\nlocalization\ninternationalization\npneumonoultramicroscopicsilicovolcanoconiosis', 'word\nl10n\ni18n\np43s', TRUE, (SELECT id FROM problems WHERE title='Слишком длинные слова'));

INSERT INTO problems (title, statement, input_format, output_format, time_limit_milliseconds, memory_limit_megabytes)
VALUES (
    'Команда', 
    'Три друга (Петя, Вася и Тоня) решили участвовать в олимпиаде. Они пишут решение задачи только в том случае, если хотя бы двое из них уверены в решении.',
    'Первая строка — число задач $n$ ($1 \le n \le 1000$). Далее $n$ строк, в каждой по три числа (0 или 1). 1 означает уверенность.',
    'Выведите количество задач, которые друзья решат.',
    2000, 256
);

INSERT INTO test_cases (input_data, output_data, is_sample, problem_id) VALUES 
('3\n1 1 0\n1 1 1\n1 0 0', '2', TRUE, (SELECT id FROM problems WHERE title='Команда'));

INSERT INTO problems (title, statement, input_format, output_format, time_limit_milliseconds, memory_limit_megabytes)
VALUES (
    'Театральная площадь', 
    'Площадь имеет размер $n \times m$ метров. Её нужно замостить плитками $a \times a$. Плитки можно класть только параллельно сторонам площади. Разрезать плитки нельзя.',
    'Вводится три целых положительных числа $n, m, a$ ($1 \le n, m, a \le 10^9$).',
    'Выведите минимальное количество плиток.',
    1000, 256
);

INSERT INTO test_cases (input_data, output_data, is_sample, problem_id) VALUES 
('6 6 4', '4', TRUE, (SELECT id FROM problems WHERE title='Театральная площадь'));

INSERT INTO problems (title, statement, input_format, output_format, time_limit_milliseconds, memory_limit_megabytes)
VALUES (
    'Следующий раунд', 
    'В следующий раунд проходят участники, набравшие не меньше баллов, чем участник на $k$-м месте, при условии, что их балл строго больше нуля.',
    'Строка содержит $n$ и $k$ ($1 \le k \le n \le 50$). Вторая строка содержит $n$ чисел — баллы участников.',
    'Выведите количество прошедших участников.',
    3000, 256
);

INSERT INTO test_cases (input_data, output_data, is_sample, problem_id) VALUES 
('8 5\n10 9 8 7 7 7 5 5', '6', TRUE, (SELECT id FROM problems WHERE title='Следующий раунд')),
('4 2\n0 0 0 0', '0', TRUE, (SELECT id FROM problems WHERE title='Следующий раунд'));

INSERT INTO contest_problems (contest_id, problem_id, problem_index) VALUES 
    (1, (SELECT id FROM problems WHERE title = 'Арбуз'), 0),               
    (1, (SELECT id FROM problems WHERE title = 'Слишком длинные слова'), 1),
    (1, (SELECT id FROM problems WHERE title = 'Команда'), 2),              
    (1, (SELECT id FROM problems WHERE title = 'Театральная площадь'), 3),  
    (1, (SELECT id FROM problems WHERE title = 'Следующий раунд'), 4);      