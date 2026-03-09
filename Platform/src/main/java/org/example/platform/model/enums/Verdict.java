package org.example.platform.model.enums;

public enum Verdict {
    PENDING,               // В очереди
    TESTING,               // Выполняется
    ACCEPTED,              // Полное решение (OK)
    WRONG_ANSWER,          // Неверный ответ (WA)
    TIME_LIMIT_EXCEEDED,   // Превышено время (TLE)
    MEMORY_LIMIT_EXCEEDED, // Превышена память (MLE)
    COMPILATION_ERROR,     // Ошибка компиляции (CE)
    RUNTIME_ERROR          // Ошибка выполнения (RE)
}