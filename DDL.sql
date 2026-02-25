CREATE TABLE organizations (
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    country VARCHAR(100)
);

CREATE TABLE users (
    id              SERIAL PRIMARY KEY,
    username        VARCHAR(100) UNIQUE NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    organization_id INT REFERENCES organizations(id) ON DELETE SET NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE problems (
    id              SERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    memory_limit_mb INT DEFAULT 256,
    time_limit_ms   INT DEFAULT 1000,
    author_id       INT REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE tags (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE contests (
    id         SERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time   TIMESTAMP NOT NULL
);

CREATE TABLE programming_languages (
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    version VARCHAR(50) NOT NULL
);

CREATE TABLE submissions (
    id             SERIAL PRIMARY KEY,
    user_id        INT REFERENCES users(id) ON DELETE CASCADE,
    problem_id     INT REFERENCES problems(id) ON DELETE CASCADE,
    language_id    INT REFERENCES programming_languages(id) ON DELETE RESTRICT,
    code_text      TEXT NOT NULL,
    status_verdict VARCHAR(50) DEFAULT 'IN_QUEUE',
    submitted_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE problem_tags (
    problem_id INT REFERENCES problems(id) ON DELETE CASCADE,
    tag_id     INT REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (problem_id, tag_id)
);

CREATE TABLE contest_problems (
    contest_id INT REFERENCES contests(id) ON DELETE CASCADE,
    problem_id INT REFERENCES problems(id) ON DELETE CASCADE,
    PRIMARY KEY (contest_id, problem_id)
);

CREATE TABLE contest_participants (
    contest_id INT REFERENCES contests(id) ON DELETE CASCADE,
    user_id    INT REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (contest_id, user_id)
);