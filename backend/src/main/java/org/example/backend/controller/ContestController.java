package org.example.backend.controller;

import org.example.backend.dto.ContestDTO;
import org.example.backend.dto.ContestProblemDTO;
import org.example.backend.dto.ProblemDTO;
import org.example.backend.entity.ContestProblem;
import org.example.backend.repository.ContestProblemRepository;
import org.example.backend.service.ContestProblemService;
import org.example.backend.service.ContestService;
import org.example.backend.utility.ProblemMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;
    private final ContestProblemService contestProblemService;
    private final ContestProblemRepository contestProblemRepository;

    public ContestController(
        ContestService contestService,
        ContestProblemService contestProblemService,
        ContestProblemRepository contestProblemRepository
    ) {
        this.contestService = contestService;
        this.contestProblemService = contestProblemService;
        this.contestProblemRepository = contestProblemRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ContestDTO>> getAllContests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(contestService.getContests(search, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContestDTO> getContestById(@PathVariable Integer id) {
        return ResponseEntity.ok(contestService.getContestById(id));
    }

    @PostMapping
    public ResponseEntity<ContestDTO> createContest(@RequestBody ContestDTO dto) {
        ContestDTO created = contestService.createContest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateContest(@PathVariable Integer id, @RequestBody ContestDTO dto) {
        contestService.updateContest(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContest(@PathVariable Integer id) {
        contestService.deleteContest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/problems")
    public ResponseEntity<List<ContestProblemDTO>> getContestProblems(@PathVariable Integer id) {
        List<ContestProblem> links = contestProblemRepository.findByContestIdOrderByProblemIndexAsc(id);

        List<ContestProblemDTO> response = links.stream()
            .map(link -> new ContestProblemDTO(
                ProblemMapper.mapProblemToDto(link.getProblem()),
                link.getProblemIndex()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/problems/link/{problemId}")
    public ResponseEntity<Void> linkProblem(@PathVariable Integer id, @PathVariable Integer problemId) {
        contestProblemService.linkProblemToContest(id, problemId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/problems/reorder")
    public ResponseEntity<Void> reorderProblems(@PathVariable Integer id, @RequestBody List<Integer> orderedProblemIds) {
        contestProblemService.reorderProblems(id, orderedProblemIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/problems/{problemId}")
    public ResponseEntity<Void> unlinkProblem(@PathVariable Integer id, @PathVariable Integer problemId) {
        contestProblemService.unlinkProblem(id, problemId);
        return ResponseEntity.noContent().build();
    }
}