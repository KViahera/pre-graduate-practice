package org.example.platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
// 1. Добавляем implements UserDetails
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // --- 2. Ниже реализуем методы интерфейса UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Добавляем сами роли
        this.roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getName()))
        );

        // Добавляем атомарные права из этих ролей
        this.roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission ->
                        authorities.add(new SimpleGrantedAuthority(permission.getName()))
                );

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash; // Указываем Spring Security, где лежит пароль
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Аккаунт не протухает
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Аккаунт не заблокирован
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Пароль не протухает
    }

    @Override
    public boolean isEnabled() {
        return this.isActive; // Берем значение из нашего поля isActive
    }
}