package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.dto.ApprovalLogItem;
import com.cb.workflow.workflow.persistence.entity.WorkflowApprovalLogEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WorkflowApprovalLogMapper {

    @Insert("""
    INSERT INTO workflow_approvals(
        tenant_id,
        instance_id,
        actor_user_id,
        actor_user_name,
        actor_role,
        action,
        from_state,
        to_state,
        comment,
        request_id
    ) VALUES (
        #{tenantId},
        #{instanceId},
        #{actorUserId},
        #{actorUserName},
        #{actorRole},
        #{action},
        #{fromState},
        #{toState},
        #{comment},
        #{requestId}
    )
""")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WorkflowApprovalLogEntity e);

    @Select("""
        SELECT
            id,
            instance_id     AS instanceId,
            actor_user_id   AS actorUserId,
            actor_user_name AS actorUserName,
            actor_role      AS actorRole,
            action,
            from_state      AS fromState,
            to_state        AS toState,
            comment,
            request_id      AS requestId,
            created_at      AS createdAt
        FROM workflow_approvals
        WHERE tenant_id = #{tenantId}
          AND instance_id = #{instanceId}
        ORDER BY id DESC
    """)
    List<ApprovalLogItem> findHistory(@Param("tenantId") Long tenantId,
                                      @Param("instanceId") Long instanceId);
}