package org.example.platform.service;

import org.example.platform.dto.*;
import org.example.platform.model.*;
import org.example.platform.repository.*;
import org.example.platform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) throw new IllegalArgumentException("Username taken");
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        Role userRole = roleRepository.findByName("ROLE_PARTICIPANT").orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(userRole);
        userRepository.save(user);
        return new AuthenticationResponse(jwtService.generateToken(user));
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username()).orElseThrow();
        return new AuthenticationResponse(jwtService.generateToken(user));
    }
}