package org.example.platform.controller;

import lombok.RequiredArgsConstructor;
import org.example.platform.dto.StandingsResponse;
import org.example.platform.service.StandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService standingsService;

    // Таблица доступна всем (в том числе без токена, если добавить путь в SecurityConfig)
    @GetMapping("/{id}/standings")
    public ResponseEntity<StandingsResponse> getStandings(@PathVariable Long id) {
        return ResponseEntity.ok(standingsService.getStandings(id));
    }
}