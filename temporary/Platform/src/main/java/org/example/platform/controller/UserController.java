package org.example.platform.controller;

import org.example.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAuthority('ASSIGN_ROLES')")
    @PostMapping("/{userId}/roles")
    public ResponseEntity<String> assignRole(@PathVariable Long userId, @RequestParam String roleName) {
        userService.assignRoleToUser(userId, roleName);
        return ResponseEntity.ok("Role assigned");
    }
}