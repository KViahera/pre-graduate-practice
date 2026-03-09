package org.example.platform.service;

import org.example.platform.model.*;
import org.example.platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }
}