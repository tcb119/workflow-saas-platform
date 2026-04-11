
---

## Workflow Example

    以下是一個簡化的 費用報銷流程（Expense Approval Workflow） 範例，
    展示 workflow engine 如何管理流程狀態轉移。

    一個典型流程可能如下：

        Employee
        │
        ▼
        DRAFT
        │ Submit
        ▼
        PENDING_MANAGER_APPROVAL
        │ Approve
        ▼
        PENDING_FINANCE_APPROVAL
        │ Approve
        ▼
        APPROVED
        
        ---

## Step-by-Step Process

### Step 1 — Create Workflow

        員工建立一筆費用報銷申請：
            POST /workflow/create
                workflow instance 初始狀態：
                    DRAFT

---

### Step 2 — Submit Request

        員工送出申請：
            action = SUBMIT
                系統會查詢 transition rule：
                    DRAFT + SUBMIT → PENDING_MANAGER_APPROVAL
                流程狀態變為：
                    PENDING_MANAGER_APPROVAL

---

### Step 3 — Manager Approval

        主管審核該申請。
            Transition rule：
                PENDING_MANAGER_APPROVAL + APPROVE → PENDING_FINANCE_APPROVAL
        系統會同時檢查：
            - 使用者是否為 Manager
            - 是否具備 requiredRole

---

### Step 4 — Finance Approval

        財務部門進行最終審核：
                PENDING_FINANCE_APPROVAL + APPROVE → APPROVED
        流程狀態變為：
                APPROVED
        此時流程完成。

---

# Workflow History Example

        系統會記錄每一次 transition：
                workflow_history
        
        範例紀錄：
                Instance 1001
                
                DRAFT
                ↓
                SUBMIT (user: employee01)
                
                PENDING_MANAGER_APPROVAL
                ↓
                APPROVE (user: manager01)
                
                PENDING_FINANCE_APPROVAL
                ↓
                APPROVE (user: finance01)

        APPROVED
                這些資料可以用來：
                
                  - 建立 approval timeline
                  - 稽核流程操作
                  - 追蹤流程進度
                
---

# How the Engine Determines the Next State

Workflow engine 透過 **transition rules table** 決定下一個狀態。
                
        資料範例：

            workflow_transitions
    
            | from_state               | action   | to_state                 | required_role |
            |--------------------------|----------|--------------------------|---------------|
            | DRAFT                    | SUBMIT   | PENDING_MANAGER_APPROVAL | USER          |
            | PENDING_MANAGER_APPROVAL | APPROVE  | PENDING_FINANCE_APPROVAL | MANAGER       |
            | PENDING_FINANCE_APPROVAL | APPROVE  | APPROVED                 | FINANCE       |

透過這個設計：

            - workflow 行為由資料控制
            - 不需要寫死流程邏輯
            - 管理員可以動態調整流程

---

# Why This Design Matters

這種 workflow engine 設計可以應用在許多企業場景，例如：
    
      - Expense Approval System
      - Procurement Workflow
      - Investment Committee Decision
      - Internal Governance Process
    
未來也可以擴展為：
    
      - voting approval
      - majority rule
      - SLA timeout
      - escalation
      - governance workflow
    
使系統成為一個 **可配置的決策流程引擎（Decision Process Engine）**

---

# System Design Highlights

    Workflow Engine 的核心設計是 Transition Rule Model。

        每個流程轉移由以下欄位定義：
        
            fromState
            action
            toState
            requiredRole
            nextAssigneeRole
            isActive
            
            例如：
            DRAFT + SUBMIT → PENDING
            PENDING + APPROVE → APPROVED
            PENDING + REJECT → REJECTED

        透過這種設計：

          - 流程邏輯可以透過資料庫設定
          - 不需要修改程式碼即可調整流程
          - 不同租戶可以擁有不同流程規則

---

# Multi-Layer Guard System

        Workflow transition 並不只是一個狀態變化，  
        而是透過多層 Guard 機制驗證。
        
            驗證順序：
            Tenant Isolation
            ↓
            Owner Validation
            ↓
            Role Authorization
            ↓
            Workflow State Validation
        
        此設計確保：
        
          - 租戶資料隔離
          - 只有合法使用者可以操作流程
          - 不符合流程狀態的操作會被拒絕
        
        相關邏輯實作於：

            WorkflowGuards

---

# Transition Rule Validation

    為避免流程配置錯誤，系統在建立或修改 rule 時會進行多層驗證：

### Input Normalization

    輸入資料會先經過標準化處理：
    trim
    uppercase normalization
    null handling

---

### Business Rule Validation

    例如：
    
        fromState 不能等於 toState
        state 長度限制
        action 長度限制

---

### Duplicate Rule Prevention

    同一個 tenant 不允許存在：
    
    fromState + action
    
    的重複 rule。

---

# Service Layer Validation Pattern

    系統在 Service 層採用以下驗證流程：
    
        Normalization
        ↓
        Business Validation
        ↓
        Uniqueness Check
        ↓
        Persistence
    
    這種設計可以確保：
    
      - 輸入資料一致
      - business rule 在 service 層集中管理
      - database constraint 作為最後防線

---

# Admin Configurable Workflow Rules

    管理員可以透過 Admin Workflow Config UI 管理 transition rules。
    
    支援：

      - 新增 workflow rule
      - 修改 workflow rule
      - 啟用 / 停用 rule
      - 防止 duplicate rules

    管理 API：

      - /admin/workflow/transitions
      - /admin/workflow/transitions
      - /admin/workflow/transitions/{id}
      - /admin/workflow/transitions/{id}/active

    這使 workflow engine 能夠 透過配置管理流程行為。

---

# Extensible Workflow Engine

    此架構可進一步擴展為：
    
        Workflow Engine
        ↓
        Governance Workflow
        ↓
        Financial Decision Engine

    未來可支援：
    
      - voting approval
      - majority rule
      - SLA timeout
      - escalation
      - deadlock resolution

    這使系統可應用於：
    
      - 金融決策流程
      - 投資審查流程
      - 組織治理流程
