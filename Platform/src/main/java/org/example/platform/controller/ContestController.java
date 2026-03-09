package org.example.platform.controller;

import lombok.RequiredArgsConstructor;
import org.example.platform.dto.ContestCreateRequest;
import org.example.platform.dto.ProblemForContestResponse;
import org.example.platform.service.ContestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    // Создавать контесты могут только пользователи с соответствующим правом
    @PreAuthorize("hasAuthority('CREATE_CONTEST')")
    @PostMapping
    public ResponseEntity<String> createContest(@RequestBody ContestCreateRequest request) {
        Long contestId = contestService.createContest(request);
        return ResponseEntity.ok("Контест успешно создан. ID: " + contestId);
    }

    // Зарегистрироваться может любой авторизованный пользователь
    @PostMapping("/{id}/register")
    public ResponseEntity<String> register(@PathVariable Long id) {
        contestService.registerForContest(id);
        return ResponseEntity.ok("Вы успешно зарегистрированы!");
    }

    // Получить список задач (логика доступа скрыта в сервисе)
    @GetMapping("/{id}/problems")
    public ResponseEntity<List<ProblemForContestResponse>> getProblems(@PathVariable Long id) {
        return ResponseEntity.ok(contestService.getContestProblems(id));
    }
}