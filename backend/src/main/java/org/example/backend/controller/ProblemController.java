package org.example.backend.controller;

import org.example.backend.dto.ProblemDTO;
import org.example.backend.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping
    public ResponseEntity<List<ProblemDTO>> searchProblems(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(problemService.searchProblems(search));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProblem(@PathVariable Integer id, @RequestBody ProblemDTO dto) {
        problemService.updateProblem(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Integer id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}