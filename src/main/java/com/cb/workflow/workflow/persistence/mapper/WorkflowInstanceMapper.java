package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.dto.InboxItem;
import com.cb.workflow.workflow.dto.WorkflowDetailResponse;
import com.cb.workflow.workflow.persistence.entity.WorkflowInstanceEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WorkflowInstanceMapper {

    @Select("""
        SELECT
            id,
            tenant_id                AS tenantId,
            owner_user_id            AS ownerUserId,
            assignee_user_id         AS assigneeUserId,
            assignee_role_code       AS assigneeRoleCode,
            state,
            version,
            title,
            last_transition_request_id AS lastTransitionRequestId,
            created_at               AS createdAt,
            updated_at               AS updatedAt
        FROM workflow_instances
        WHERE tenant_id = #{tenantId}
          AND id = #{id}
    """)
    WorkflowInstanceEntity findByTenantAndId(@Param("tenantId") Long tenantId,
                                             @Param("id") Long id);

    // optimistic locking（樂觀鎖）：WHERE version = #{expectedVersion}
    @Update("""
        UPDATE workflow_instances
        SET
            state = #{newState},
            assignee_user_id = #{newAssigneeUserId},
            assignee_role_code = #{newAssigneeRoleCode},
            version = version + 1,
            last_transition_request_id = #{requestId},
            updated_at = CURRENT_TIMESTAMP
        WHERE tenant_id = #{tenantId}
          AND id = #{id}
          AND version = #{expectedVersion}
          AND (
                last_transition_request_id IS NULL
                OR last_transition_request_id <> #{requestId}
              )
    """)
    int updateStateWithOptimisticLock(
            @Param("tenantId") Long tenantId,
            @Param("id") Long id,
            @Param("expectedVersion") Long expectedVersion,
            @Param("newState") String newState,
            @Param("newAssigneeUserId") Long newAssigneeUserId,
            @Param("newAssigneeRoleCode") String newAssigneeRoleCode,
            @Param("requestId") String requestId
    );

    @Select("""
        SELECT
            id                  AS instanceId,
            state               AS state,
            owner_user_id       AS ownerId,
            assignee_user_id    AS assigneeUserId,
            assignee_role_code  AS assigneeRoleCode,
            updated_at          AS updatedAt
        FROM workflow_instances
        WHERE tenant_id = #{tenantId}
          AND id = #{instanceId}
        LIMIT 1
    """)
        WorkflowDetailResponse findDetail(@Param("tenantId") Long tenantId,
                                          @Param("instanceId") Long instanceId);

    @Insert("""
        INSERT INTO workflow_instances (
            tenant_id,
            owner_user_id,
            assignee_user_id,
            assignee_role_code,
            state,
            version,
            last_transition_request_id,
            title,
            created_at,
            updated_at
        ) VALUES (
            #{tenantId},
            #{ownerUserId},
            #{assigneeUserId},
            #{assigneeRoleCode},
            #{state},
            #{version},
            #{lastTransitionRequestId},
            #{title},
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
        )
    """)
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(WorkflowInstanceEntity entity);

    @Select("""
    SELECT
        id                  AS instanceId,
        state               AS state,
        owner_user_id       AS ownerId,
        assignee_user_id    AS assigneeUserId,
        assignee_role_code  AS assigneeRoleCode,
        updated_at          AS updatedAt
    FROM workflow_instances
    WHERE tenant_id = #{tenantId}
      AND owner_user_id = #{userId}
    ORDER BY updated_at DESC
    LIMIT #{size} OFFSET #{offset}
""")
    List<InboxItem> findMyRequests(@Param("tenantId") Long tenantId,
                                   @Param("userId") Long userId,
                                   @Param("size") int size,
                                   @Param("offset") int offset);
}