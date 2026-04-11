
---

# API Design

本系統採用 RESTful API 設計，將 workflow 操作分為三個主要類型：

    - Workflow Instance API
    - Workflow Transition API 
    - Admin Workflow Configuration API

    所有 API 均透過 JWT Authentication 進行身份驗證。

## Workflow Instance API

此類 API 用於建立與查詢 workflow instance。

### Create Workflow

建立新的 workflow instance。
POST /workflow/create

    Request Example
        {
        "title": "Expense Reimbursement",
        "description": "Travel expense request"
        }
    
    Response Example
        {
        "instanceId": 1001,
        "state": "DRAFT"
        }

### Workflow Detail

查詢 workflow instance 詳細資訊。
GET /workflow/detail?instanceId=1001

    Response Example
        {
        "instanceId": 1001,
        "state": "PENDING_MANAGER_APPROVAL",
        "createdBy": 12,
        "createdAt": "2026-03-01T10:00:00"
        }

### Workflow History

查詢 workflow instance 的歷史操作紀錄。
GET /workflow/history?instanceId=1001

    Response Example
    [
        {
            "action": "SUBMIT",
            "fromState": "DRAFT",
            "toState": "PENDING_MANAGER_APPROVAL",
            "actorId": 12,
            "createdAt": "2026-03-01T10:05:00"
        }
    ]

## Workflow Transition API

此類 API 用於執行 workflow state transition。

Execute Transition

執行 workflow transition。
POST /workflow/transition

    Request Example
        {
        "instanceId": 1001,
        "action": "APPROVE"
        }

系統會依照 transition rule 決定下一個 state。

    例如：
        PENDING_MANAGER_APPROVAL + APPROVE → PENDING_FINANCE_APPROVAL

    Response Example
        {
        "instanceId": 1001,
        "state": "PENDING_FINANCE_APPROVAL"
        }

## Query APIs

### Inbox
查詢使用者待處理流程。
GET /workflow/inbox

    用途：
      - 查詢目前需要審核的流程
      - 建立審核工作清單

### My Requests
查詢使用者建立的 workflow instance。
GET /workflow/my-requests

    用途：
      - 查看自己建立的流程
      - 追蹤流程狀態

## Admin Workflow Configuration API
此類 API 用於管理 workflow transition rules。

List Transition Rules

    查詢所有 transition rules。
        GET /admin/workflow/transitions

Create Transition Rule

    新增 workflow transition rule。
        POST /admin/workflow/transitions

        Request Example
            {
            "fromState": "DRAFT",
            "action": "SUBMIT",
            "toState": "PENDING_MANAGER_APPROVAL",
            "requiredRole": "USER",
            "nextAssigneeRoleCode": "MANAGER"
            }

Update Transition Rule

    修改既有 transition rule。
        PUT /admin/workflow/transitions/{id}

Enable / Disable Rule

    啟用或停用 transition rule。
        PATCH /admin/workflow/transitions/{id}/active

        用途：
          - 暫時停用流程規則
          - 測試新的 workflow configuration

## API Security
所有 API 皆透過 JWT Authentication 保護。

    Authentication header：
        Authorization: Bearer <JWT_TOKEN>

後端會透過：

    JwtAuthenticationFilter

解析 token 並建立：

    AuthPrincipal

其中包含：

    userId
    tenantId
    roles

這些資訊會用於 workflow transition 的授權檢查。