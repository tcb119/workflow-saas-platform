package com.cb.workflow.workflow.persistence.mapper;

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
            tenant_id, 
            instance_id, 
            actor_user_id, 
            action, 
            from_state, 
            to_state, 
            comment, 
            created_at
        FROM workflow_approval_logs
        WHERE tenant_id = #{tenantId} AND instance_id = #{instanceId}
        ORDER BY id DESC
        LIMIT #{limit}
    """)
    List<WorkflowApprovalLogEntity> listByInstance(@Param("tenantId") Long tenantId,
                                                   @Param("instanceId") Long instanceId,
                                                   @Param("limit") int limit);
}