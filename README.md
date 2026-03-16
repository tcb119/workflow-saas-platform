
# Workflow SaaS Platform
A configurable workflow engine with RBAC authorization and multi-tenant architecture.

---

# **Architecture Diagram**

                ┌───────────────────────┐
                │        Frontend       │
                │        Vue + Vite     │
                │                       │
                │  - Login UI           │
                │  - Inbox              │
                │  - My Requests        │
                │  - Workflow Detail    │
                │  - Admin Config UI    │
                └───────────┬───────────┘
                            │
                            │ HTTP / REST API
                            ▼
                ┌───────────────────────┐
                │      Spring Boot      │
                │        Backend        │
                │                       │
                │  Controller Layer     │
                │  Service Layer        │
                │  Security Layer       │
                └───────────┬───────────┘
                            │
                            │
                            ▼
              ┌────────────────────────────┐
              │      Workflow Engine       │
              │                            │
              │  - Workflow Instance       │
              │  - Transition Engine       │
              │  - Approval Guard          │
              │  - Rule Validation         │
              │                            │
              └───────────┬────────────────┘
                          │
                          │
                          ▼
              ┌────────────────────────────┐
              │      Authorization Layer   │
              │                            │
              │  - JWT Authentication      │
              │  - RBAC Authorization      │
              │  - Tenant Isolation        │
              │                            │
              └───────────┬────────────────┘
                          │
                          ▼
              ┌────────────────────────────┐
              │          Database          │
              │            MySQL           │
              │                            │
              │  tables                    │
              │  - workflow_instances      │
              │  - workflow_transitions    │
              │  - workflow_history        │
              │  - users / roles           │
              │  - tenants                 │
              │                            │
              └────────────────────────────┘

## Overview

Workflow SaaS Platform 是一個以後端為核心設計的工作流程系統（Workflow Engine），  
實作了可配置的 **流程狀態轉移機制（State Transition Engine）**，並結合：
    
      - RBAC 權限控制（Role-Based Access Control）
      - 多租戶架構（Multi-Tenant Architecture）
      - 工作流程實例管理（Workflow Instance）
      - 管理者可配置的 Transition Rules
    
此專案模擬企業常見的 **簽核流程系統 / Workflow SaaS 系統**，  
並展示後端如何設計 **可擴展的流程引擎架構**。

---

# Core Features

## Workflow Instance Management

    使用者可以建立 workflow instance 並追蹤流程狀態。

        主要功能包含：
        
        - 建立流程（Create Workflow Instance）
          - 查詢流程詳情（Workflow Detail）
          - 查看流程歷史（Workflow History）
          - 查詢個人送出的流程（My Requests）
          - 查詢待處理流程（Inbox）
        
        API 範例：
        POST /workflow/create
        GET /workflow/detail
        GET /workflow/history
        GET /workflow/my-requests
        GET /workflow/inbox

---

## Workflow Transition Engine

    系統核心為 **Workflow Transition Engine**，負責控制流程狀態如何轉移。

        例如：
            DRAFT → SUBMIT → PENDING → APPROVED
        
        每一個 transition rule 由以下欄位定義：
        
        - fromState
          - action
          - toState
          - requiredRole
          - nextAssigneeRole
        
        範例：
        fromState: DRAFT
        action: SUBMIT
        toState: PENDING
        requiredRole: USER
        nextAssigneeRole: ADMIN
        
    透過這種設計，流程轉移可以透過資料庫配置，而不需要修改程式碼。

---

## Admin Workflow Configuration

    管理員可以透過管理介面配置 workflow transition rules。
    
        支援以下操作：
        
        - 新增 transition rule
          - 修改 transition rule
          - 啟用 / 停用 rule
          - 防止 duplicate rule
        
        管理 API：
        GET    /admin/workflow/transitions
        POST   /admin/workflow/transitions
        PUT    /admin/workflow/transitions/{id}
        PATCH  /admin/workflow/transitions/{id}/active
    
    此設計使流程引擎具備高度可配置性。

---

# RBAC Authorization Guard

    流程轉移時會經過多層授權檢查：
    
        - Tenant Isolation（租戶隔離）
          - Owner Validation（確認流程建立者）
          - Role Authorization（角色權限）
          - Workflow State Validation（流程狀態檢查）
        
        例如：
            只有 ADMIN 可以批准 PENDING 狀態的流程
    
    相關邏輯實作於：

        WorkflowGuards

---

# Tech Stack

    Backend
            Java 17
            Spring Boot
            Spring Security
            MyBatis
            JWT
            MySQL
    
    Frontend
            Vue
            Axios
            Vite

---

# Key Engineering Challenges

    設計可配置的 Workflow Engine:
    
        本專案的一個核心挑戰，是設計一套 可配置的工作流程系統（Configurable Workflow System），
        使流程的狀態轉移邏輯可以透過設定來定義，而不是寫死在程式碼中。
    
        為了達成這個目標，系統引入了 Transition Rule Model（流程轉移規則模型）：

        •	fromState + action → toState

        透過這個模型，每一個流程轉移都由資料庫中的規則定義。

        
        這樣的設計使得系統管理者可以：

        •	修改流程邏輯
        •	調整審核流程
        •	新增或停用 transition rule
        
        而不需要重新修改或部署應用程式。
        
        這使 workflow engine 具備 高度彈性與可配置性。


    防止無效的 Workflow 狀態轉移:

        在工作流程系統中，確保流程狀態的一致性與合法性非常重要。
    
        因此在執行 workflow transition 時，
        系統會透過 多層 Guard 機制（Multi-layer Guard System） 進行驗證：

        •	Tenant Isolation
        •	Owner Validation
        •	Role Authorization
        •	Workflow State Validation

        各層檢查包含：
            •	Tenant Isolation
                    確保不同租戶（tenant）的資料完全隔離。
            •	Owner Validation
                    確認流程操作者是否為該流程的合法參與者。
            •	Role Authorization
                    確認使用者角色是否具備執行該 transition 的權限。
            •	Workflow State Validation
                    確認當前流程狀態是否允許執行指定 action。

        透過這些驗證機制，系統可以：

        •	保持 workflow 狀態的一致性
        •	防止非法或錯誤的流程操作
        •	確保流程執行符合預期的業務邏輯。

---

# Learning Goals
此專案主要探索：

          - Workflow Engine 設計
          - SaaS 多租戶架構
          - RBAC 權限模型
          - Backend 系統架構設計
          - 前後端整合

---

# Documentation

Detailed design documents:

| Document                                   | Description                                  |
|--------------------------------------------|----------------------------------------------|
| [Architecture](docs/ARCHITECTURE.md)       | System architecture and module structure     |
| [Workflow Engine](docs/WORKFLOW_ENGINE.md) | Workflow transition engine design            |
| [Database Schema](docs/DATABASE_SCHEMA.md) | Database schema and entity relationships     |
| [API Design](docs/API_DESIGN.md)           | REST API specification                       |
| [Security Model](docs/SECURITY_MODEL.md)   | Authentication and RBAC authorization        |
| [Future Roadmap](docs/FUTURE_ROADMAP.md)   | Future extensibility and governance workflow |

---

# Author

    Caleb Chang
    
    Backend / Solution-oriented Engineer

----