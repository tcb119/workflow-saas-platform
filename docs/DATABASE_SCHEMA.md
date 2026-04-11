
---

# Database Schema Diagram

本專案的資料庫設計圍繞在 Workflow Engine、RBAC 權限模型 與 Multi-Tenant Architecture 三個核心概念。
    
        主要資料表包含：
          - tenants
          - users
          - roles
          - user_roles
          - workflow_instances
          - workflow_transitions
          - workflow_history
        
        這些資料表共同支撐：
          - 多租戶資料隔離
          - 使用者身份與角色授權
          - 流程實例管理
          - 流程轉移規則配置
          - 流程歷史紀錄與審計追蹤

    
Database Tables:

## tenants
    租戶資料表，用來表示不同企業或組織。
    
    每一筆 workflow 資料都會綁定：
        tenant_id

    以確保不同租戶之間資料完全隔離。

## users
    使用者資料表，儲存登入帳號與基本資訊。
    
    用途：
      - 身份認證
      - 對應 workflow 建立者 / 審批者
      - 與角色系統整合

## roles
    角色資料表，用來定義系統中的角色，例如：
    
       USER
       ADMIN
       MANAGER
       FINANCE
    
    這些角色會用於 workflow transition 的授權判斷。

## user_roles
    使用者與角色的關聯表。
    
    一個使用者可以擁有多個角色，例如：

        ROLE_ADMIN
        ROLE_USER
    
    用來支援：
      - RBAC
      - 審批權限控制
      - 後台管理權限

## workflow_instances
    流程實例資料表，代表一筆實際執行中的 workflow。

    例如：
      - 一筆報銷申請
      - 一筆採購請求
      - 一筆內部決議流程

    主要欄位：
      - tenant_id
      - owner_user_id
      - state
      - title
      - version
      - assignee_user_id
      - assignee_role_code

## workflow_transitions
    流程轉移規則表，是 workflow engine 的核心之一。

        每一筆 rule 定義：
        
            from_state + action → to_state
        
        例如： 
            DRAFT + SUBMIT → PENDING
            PENDING + APPROVE → APPROVED

        主要欄位：
          - from_state
          - action
          - to_state
          - required_role
          - next_assignee_role_code
          - is_active
        
        此表支援：
          - 可配置流程規則
          - Admin 後台管理
          - 不需改 code 即可調整流程行為

## workflow_history
        流程歷史紀錄表，用來記錄每一次 transition。
        
        例如：
          - 誰送出流程
          - 誰批准流程
          - 從哪個 state 到哪個 state
          - 操作時間
        
        這張表用於：
          - approval timeline
          - audit trail
          - 流程追蹤

### Entity Relationship Summary

        整體資料模型關係如下：

          - 一個 tenant 可以擁有多個 users
          - 一個 tenant 可以擁有多個 workflow_instances
          - 一個 tenant 可以擁有多個 workflow_transitions
          - 一個 user 可以透過 user_roles 對應多個 roles
          - 一個 workflow_instance 可以對應多筆 workflow_history
          - 一個 workflow_instance 的狀態轉移，會由 workflow_transitions 提供規則判斷