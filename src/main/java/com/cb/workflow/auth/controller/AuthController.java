package com.cb.workflow.auth.controller;

import com.cb.workflow.auth.dto.LoginRequest;
import com.cb.workflow.auth.dto.LogoutRequest;
import com.cb.workflow.auth.dto.RefreshRequest;
import com.cb.workflow.auth.dto.TokenResponse;
import com.cb.workflow.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestParam Long tenantId,
            @RequestBody @Valid RefreshRequest req
    ) {
        return ResponseEntity.ok(authService.refresh(tenantId, req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestParam Long tenantId,
            @RequestBody @Valid LogoutRequest req
    ) {
        authService.logout(tenantId, req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}