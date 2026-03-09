-- 1. Добавляем базовые права (permissions)
INSERT INTO permissions (name, description) VALUES 
    ('SUBMIT_CODE', 'Отправка решений на проверку'),
    ('CREATE_PROBLEM', 'Создание и редактирование собственных задач'),
    ('CREATE_CONTEST', 'Создание соревнований и управление ими'),
    ('MANAGE_GROUPS', 'Создание приватных тренировочных групп'),
    ('MODERATE_CONTENT', 'Удаление чужих комментариев и блогов'),
    ('BAN_USER', 'Выдача предупреждений и блокировка пользователей'),
    ('ASSIGN_ROLES', 'Управление ролями пользователей системы')
ON CONFLICT (name) DO NOTHING;

-- 2. Добавляем основные роли (roles)
INSERT INTO roles (name, description) VALUES 
    ('ROLE_PARTICIPANT', 'Базовый участник платформы'),
    ('ROLE_PROBLEM_SETTER', 'Автор задач'),
    ('ROLE_GROUP_MANAGER', 'Преподаватель или менеджер учебной группы'),
    ('ROLE_MODERATOR', 'Модератор сообщества'),
    ('ROLE_ADMIN', 'Администратор системы')
ON CONFLICT (name) DO NOTHING;

-- 3. Связываем роли и права в таблице role_permissions
-- Используем подзапросы, чтобы не зависеть от автосгенерированных ID, которые могут меняться
INSERT INTO role_permissions (role_id, permission_id) VALUES
    -- Права участника
    ((SELECT id FROM roles WHERE name = 'ROLE_PARTICIPANT'), (SELECT id FROM permissions WHERE name = 'SUBMIT_CODE')),

    -- Права автора задач (наследует права участника + свои)
    ((SELECT id FROM roles WHERE name = 'ROLE_PROBLEM_SETTER'), (SELECT id FROM permissions WHERE name = 'SUBMIT_CODE')),
    ((SELECT id FROM roles WHERE name = 'ROLE_PROBLEM_SETTER'), (SELECT id FROM permissions WHERE name = 'CREATE_PROBLEM')),
    ((SELECT id FROM roles WHERE name = 'ROLE_PROBLEM_SETTER'), (SELECT id FROM permissions WHERE name = 'CREATE_CONTEST')),

    -- Права преподавателя
    ((SELECT id FROM roles WHERE name = 'ROLE_GROUP_MANAGER'), (SELECT id FROM permissions WHERE name = 'SUBMIT_CODE')),
    ((SELECT id FROM roles WHERE name = 'ROLE_GROUP_MANAGER'), (SELECT id FROM permissions WHERE name = 'MANAGE_GROUPS')),

    -- Права модератора
    ((SELECT id FROM roles WHERE name = 'ROLE_MODERATOR'), (SELECT id FROM permissions WHERE name = 'SUBMIT_CODE')),
    ((SELECT id FROM roles WHERE name = 'ROLE_MODERATOR'), (SELECT id FROM permissions WHERE name = 'MODERATE_CONTENT')),
    ((SELECT id FROM roles WHERE name = 'ROLE_MODERATOR'), (SELECT id FROM permissions WHERE name = 'BAN_USER')),

    -- Права администратора (выдаем права на управление ролями)
    ((SELECT id FROM roles WHERE name = 'ROLE_ADMIN'), (SELECT id FROM permissions WHERE name = 'ASSIGN_ROLES'))
ON CONFLICT DO NOTHING;

-- 4. Базовый набор тегов для алгоритмических задач
INSERT INTO tags (name) VALUES 
    ('math'),
    ('dp'),                  -- динамическое программирование
    ('graphs'),              -- графы
    ('greedy'),              -- жадные алгоритмы
    ('brute force'),         -- полный перебор
    ('data structures'),     -- структуры данных
    ('binary search'),       -- бинарный поиск
    ('strings'),             -- строки
    ('geometry'),            -- вычислительная геометрия
    ('number theory')        -- теория чисел
ON CONFLICT (name) DO NOTHING;