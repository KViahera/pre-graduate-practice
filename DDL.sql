-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Таблица ролей (например, PARTICIPANT, PROBLEM_SETTER, ADMIN)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Таблица конкретных прав (например, CREATE_PROBLEM, BAN_USER, SUBMIT_CODE)
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

-- Связующая таблица: Пользователи <-> Роли
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Связующая таблица: Роли <-> Права
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Таблица тегов
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Таблица задач
CREATE TABLE problems (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    statement TEXT NOT NULL,
    input_format TEXT NOT NULL,
    output_format TEXT NOT NULL,
    time_limit_millis INTEGER NOT NULL,
    memory_limit_mb INTEGER NOT NULL,
    difficulty INTEGER,
    is_public BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Связующая таблица (Задачи <-> Теги)
CREATE TABLE problem_tags (
    problem_id BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (problem_id, tag_id)
);

-- Таблица тестов (Test Cases)
CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    input_data TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_sample BOOLEAN DEFAULT FALSE NOT NULL,
    score_weight INTEGER DEFAULT 0,
    problem_id BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE
);

-- Таблица контестов (соревнований)
CREATE TABLE contests (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INTEGER NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- Промежуточная таблица: Контест <-> Задача
-- Хранит контекст задачи внутри конкретного соревнования
CREATE TABLE contest_problems (
    id BIGSERIAL PRIMARY KEY,
    contest_id BIGINT NOT NULL REFERENCES contests(id) ON DELETE CASCADE,
    problem_id BIGINT NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
    problem_index VARCHAR(10) NOT NULL, -- Буква задачи (A, B, C1)
    max_score INTEGER NOT NULL,         -- Стоимость задачи в баллах
    UNIQUE (contest_id, problem_index), -- В одном контесте не может быть двух задач "A"
    UNIQUE (contest_id, problem_id)     -- Одну задачу нельзя добавить в контест дважды
);

-- Промежуточная таблица: Контест <-> Пользователь (Регистрация)
CREATE TABLE contest_registrations (
    id BIGSERIAL PRIMARY KEY,
    contest_id BIGINT NOT NULL REFERENCES contests(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (contest_id, user_id)        -- Защита от двойной регистрации
);