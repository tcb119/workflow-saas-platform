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
  // @Valid 用來觸發 DTO 上的驗證註解（如 @NotBlank），在進入 service 層之前先確保輸入資料合法。
  // 👉 避免：null、空字串、格式錯誤，在 Controller 層就擋掉錯誤資料
  public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {

        // ResponseEntity<> 是什麼？用來「完整控制 HTTP response」
        // 包含：status code（200 / 400 / 401）、header、body
        // HTTP status code 是 API contract 的一部分

        /*
        * 這個 API 採用 RESTful 設計，透過 @PostMapping("/login") 定義登入端點。
        * Controller 使用 @RequestBody 將前端傳來的 JSON request 轉換成 LoginRequest DTO，並透過 @Valid 觸發欄位驗證，
        * 例如 @NotBlank，確保資料在進入 service 層之前是合法的。
        * 最後回傳使用 ResponseEntity，可以明確控制 HTTP status code，
        * 這裡使用 ResponseEntity.ok() 表示成功並回傳 200 狀態與 token 資料。*/
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