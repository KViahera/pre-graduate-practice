TRUNCATE TABLE 
    contest_participants, 
    contest_problems, 
    problem_tags, 
    submissions, 
    contests, 
    tags, 
    problems, 
    programming_languages, 
    users, 
    organizations 
RESTART IDENTITY CASCADE;

INSERT INTO organizations (name, country) VALUES
    ('BSUIR', 'Belarus'),
    ('BSU', 'Belarus'),
    ('ITMO University', 'Russia');

INSERT INTO users (username, email, password_hash, organization_id) VALUES
    ('tourist', 'tourist@example.com', 'wV/XUt7sPnaYJZdg4EbcwuOF8LCGHLd0qiBw1+VIspU=', 1),
    ('kviahera', 'kviahera@example.com', '2OQP7kOURLyp/2aHembJOHPhzKoirRO9joPwuzdUAGw=', 2);

INSERT INTO programming_languages (name, version) VALUES
    ('C++', 'GCC 13.2'),
    ('Java', 'OpenJDK 21'),
    ('Python', '3.12.2');

INSERT INTO tags (name) VALUES
    ('Dynamic Programming'),
    ('Graphs'),
    ('Math'),
    ('Concurrency'),
    ('Data Structures');

INSERT INTO problems (title, description, memory_limit_mb, time_limit_ms, author_id) VALUES
    ('A+B Problem', 'Calculate the sum of two integers A and B.', 256, 1000, 1),
    ('Shortest Path', 'Find the shortest path in a directed weighted graph.', 512, 2000, 1),
    ('Dining Philosophers', 'Solve the classic synchronization problem avoiding deadlocks.', 256, 1500, 1);

INSERT INTO contests (title, start_time, end_time) VALUES
    ('Spring Open 2026', '2026-03-15 10:00:00', '2026-03-15 15:00:00'),
    ('Educational Round 1', '2026-03-20 17:35:00', '2026-03-20 19:35:00');

INSERT INTO submissions (user_id, problem_id, language_id, code_text, status_verdict) VALUES
    (2, 1, 1, '#include <iostream>\nusing namespace std;\nint main() {\n    int a, b;\n    cin >> a >> b;\n    cout << a + b << endl;\n    return 0;\n}', 'OK');

INSERT INTO problem_tags (problem_id, tag_id) VALUES
    (1, 3),
    (2, 2),
    (2, 5),
    (3, 4);

INSERT INTO contest_problems (contest_id, problem_id) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1);

INSERT INTO contest_participants (contest_id, user_id) VALUES
    (1, 2),
    (2, 2);