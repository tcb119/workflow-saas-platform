package com.cb.workflow.security.principal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthPrincipal {
    private Long tenantId;   // tenant isolation（租戶隔離）的關鍵
    private Long userId;     // subject（主體）
    private String email;    // 方便審計/顯示
}