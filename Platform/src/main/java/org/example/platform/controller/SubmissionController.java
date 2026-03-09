package org.example.platform.controller;

import lombok.RequiredArgsConstructor;
import org.example.platform.dto.SubmissionRequest;
import org.example.platform.dto.SubmissionResponse;
import org.example.platform.service.SubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PreAuthorize("hasAuthority('SUBMIT_CODE')")
    @PostMapping
    public ResponseEntity<String> submitCode(@RequestBody SubmissionRequest request) {
        Long submissionId = submissionService.submitCode(request);
        return ResponseEntity.ok("Посылка успешно отправлена. ID: " + submissionId);
    }

    // Просмотр статуса посылки (можно сделать публичным или ограничить по автору)
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmissionStatus(id));
    }
}