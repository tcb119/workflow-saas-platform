
---

# System Architecture

    系統採用分層式後端架構：

        Controller
        ↓
        Service
        ↓
        Mapper (MyBatis)
        ↓
        Database

    專案主要 package 結構：

        com.cb.workflow
        │
        ├─ auth
        │
        ├─ security
        │   ├─ jwt
        │   ├─ config
        │   └─ guard
        │
        ├─ workflow
        │   ├─ controller
        │   ├─ service
        │   ├─ dto
        │   ├─ persistence
        │   │   ├─ entity
        │   │   └─ mapper
        │
        └─ common

---

# SaaS Multi-Tenant Architecture

    系統支援 **多租戶架構（Multi-Tenant）**。
    
    每一筆 workflow rule 與 instance 都包含：
    
        tenant_id
    
    這使得：
    
      - 不同公司可使用同一系統
      - 每個 tenant 可以有自己的 workflow rules
      - 資料完全隔離

---

# Database Design

    核心資料表：
        workflow_instances
        workflow_transitions
        workflow_history
        users
        roles
        user_roles
        tenants

    transition table 範例：

        workflow_transitions
            id
            tenant_id
            from_state
            action
            to_state
            required_role
            next_assignee_role_code
            is_active

    資料一致性由以下層級共同保護：
        
          - DTO validation
          - service validation
          - database constraint

---

# Security

    系統使用以下安全機制：
    
          - Spring Security
          - JWT Authentication
          - Role-Based Access Control (RBAC)
    
    主要安全元件：
    
            JwtAuthenticationFilter
            JwtService
            AuthPrincipal
            AuthorizationGuards

---

# Repository Structure

    本專案採用 模組化的後端架構設計，將不同功能模組分離，
    使系統在擴展 workflow engine 與安全機制時能保持清晰的結構。

        專案主要結構如下：

        src/main/java/com/cb/workflow
        │
        ├─ auth
        │   ├─ controller
        │   ├─ service
        │   └─ dto
        │
        ├─ security
        │   ├─ config
        │   ├─ jwt
        │   ├─ principal
        │   └─ guard
        │
        ├─ workflow
        │   ├─ controller
        │   ├─ service
        │   ├─ dto
        │   └─ persistence
        │       ├─ entity
        │       └─ mapper
        │
        └─ common
        ├─ error
        ├─ util
        └─ web


## auth Module

    負責系統的 使用者登入與認證流程。

        主要功能：
        - 使用者登入
        - JWT token 生成
        - refresh token 機制
        
        主要元件：
            AuthController
            AuthService
            LoginRequest / LoginResponse

## security Module

    負責整個系統的 安全機制與授權控制。
    
        子模組包含：
            config
            SecurityConfig

        設定 Spring Security 的安全策略。

### jwt

    JwtService
    JwtAuthenticationFilter
    JwtClaims

    負責：
    - JWT token 解析
    - request authentication
    - token 驗證

### principal

    AuthPrincipal

        代表登入使用者的身份資訊，例如：
          - userId
          - tenantId
          - roles

### guard

    AuthorizationGuards
    WorkflowGuards

        負責 workflow transition 的授權檢查，例如：
          - tenant isolation
          - role validation
          - workflow state validation


## workflow Module

    workflow module 是本專案的 核心業務邏輯模組。

        主要負責：
          - workflow instance 管理
          - workflow transition
          - approval history
          - transition rule configuration

### controller

    提供 REST API，例如：

        WorkflowController
        AdminWorkflowConfigController
    
    API 範例：
        POST /workflow/create
        POST /workflow/transition
        GET  /workflow/detail
        GET  /workflow/history

### service

    包含 workflow 的核心邏輯，例如：

        WorkflowCommandService
        WorkflowQueryService
        AdminWorkflowConfigService

        這些 service 負責：
          - transition validation
          - rule enforcement
          - workflow state update

### dto

    定義 API request / response 的資料結構，例如：

        CreateWorkflowRequest
        WorkflowDetailResponse
        CreateTransitionRuleRequest
        UpdateTransitionRuleRequest

        DTO 的作用是：
        - 定義 API contract
        - 隔離 domain entity

### persistence

    負責資料存取層，包含：
        
        entity
    
    資料庫對應物件，例如：
        WorkflowInstanceEntity
        WorkflowTransitionEntity
        WorkflowHistoryEntity

### mapper

    使用 MyBatis 實作資料庫操作，例如：
    
        WorkflowInstanceMapper
        WorkflowTransitionMapper
        WorkflowHistoryMapper


## common Module

    common module 提供 共用工具與基礎元件。
    
    子模組包含：

### error

        ApiError
        ErrorCode
        GlobalExceptionHandler
    
    負責：
        - 統一錯誤格式
        - exception handling

    ### util
    
    通用工具類，例如：
        - string utilities
        - time utilities

### web

        ApiResponse

    統一 API response 結構，例如：

        {
        "success": true,
        "data": ...
        }


## Layered Architecture

本專案採用分層式架構：

        Controller Layer
        ↓
        Service Layer
        ↓
        Persistence Layer
        ↓
        Database

這樣的設計可以：

    - 分離業務邏輯
    - 降低模組耦合
    - 提升系統可維護性

## Why This Structure

此架構設計有幾個優點：

    - 清楚分離 security / workflow / auth 模組
    - workflow engine 的邏輯集中在 service layer
    - persistence 層與 domain 邏輯解耦
    - 方便未來擴展 workflow 功能

例如：

    - voting workflow
    - SLA timeout
    - escalation
    - governance workflow
