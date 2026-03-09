package com.kombaos.auth.controller;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/api/auth/login")
    public AuthLoginResponse login(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new AuthLoginResponse(authentication.getName(), roles);
    }

    public record AuthLoginResponse(String username, List<String> roles) {
    }
}
