
---

本系統採用 **JWT Authentication + RBAC Authorization** 的安全架構，
並結合 Workflow Guard 機制來保護 workflow transition 的合法性。

    系統安全設計分為三個層級：

      - Authentication（身份驗證）
      - Authorization（權限控制）
      - Workflow Guard（流程層授權）

---

# Authentication

系統使用 **JWT (JSON Web Token)** 作為身份驗證機制。

登入成功後，後端會產生 JWT token，
並在後續 API request 中透過 Authorization header 傳送。

Example:

    Authorization: Bearer <JWT_TOKEN>

Request 會經過：

    JwtAuthenticationFilter

該 filter 會：

      - 解析 JWT
      - 驗證 token
      - 建立使用者身份資訊

並將使用者資料封裝為：

    AuthPrincipal

其中包含：

    - userId
    - tenantId
    - roles

這些資訊會被用於後續的權限檢查。

---

# RBAC Authorization Model
系統使用 **Role-Based Access Control (RBAC)**。

    資料模型：

        users
        roles
        user_roles

一個使用者可以擁有多個角色，例如：

        USER
        ADMIN
        MANAGER
        FINANCE

Workflow transition 時會檢查：
        
    requiredRole

    例如：
        PENDING → APPROVE
        requiredRole = ADMIN

只有擁有 `ADMIN` 角色的使用者可以執行此 transition。

---


# Workflow Authorization Guard

除了 RBAC 角色驗證外，workflow transition 仍需要通過
**Workflow Guard 機制**。

WorkflowGuards 會檢查：

        Tenant Isolation
        Owner Validation
        Role Authorization
        Workflow State Validation

這些驗證確保：

      - 不同 tenant 的資料完全隔離
      - 只有合法參與者可以操作 workflow
      - 使用者角色符合 transition rule
      - 當前 workflow state 允許該 action

WorkflowGuards 實作於：

        security.guard.WorkflowGuards

---

# Security Architecture Summary

整體安全流程如下：

        Client Request
        ↓
        JwtAuthenticationFilter
        ↓
        AuthPrincipal 建立
        ↓
        Spring Security Context
        ↓
        Service Layer
        ↓
        WorkflowGuards
        ↓
        Workflow Transition

此架構確保：

      - API 需要合法身份
      - 使用者具備必要角色
      - workflow transition 符合 business rule