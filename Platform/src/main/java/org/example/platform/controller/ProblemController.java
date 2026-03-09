package org.example.platform.controller;

import org.example.platform.dto.ProblemCreateRequest;
import org.example.platform.dto.ProblemSummaryResponse;
import org.example.platform.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // Этот эндпоинт доступен ВСЕМ авторизованным пользователям
    @GetMapping
    public ResponseEntity<List<ProblemSummaryResponse>> getAllProblems() {
        return ResponseEntity.ok(problemService.getAllPublicProblems());
    }

    // Этот эндпоинт доступен ТОЛЬКО авторам задач и админам
    @PreAuthorize("hasAuthority('CREATE_PROBLEM')")
    @PostMapping
    public ResponseEntity<String> createProblem(@RequestBody ProblemCreateRequest request) {
        Long problemId = problemService.createProblem(request);
        return ResponseEntity.ok("Задача успешно создана. ID: " + problemId);
    }
}