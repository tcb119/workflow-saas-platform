package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface WorkflowTransitionMapper {

    @Select("""
        SELECT
            id,
            tenant_id                  AS tenantId,
            from_state                 AS fromState,
            action,
            to_state                   AS toState,
            required_role              AS requiredRole,
            next_assignee_user_id      AS nextAssigneeUserId,
            next_assignee_role_code    AS nextAssigneeRoleCode,
            is_active                  AS isActive
        FROM workflow_transitions
        WHERE tenant_id = #{tenantId}
          AND from_state = #{fromState}
          AND action = #{action}
          AND is_active = 1
        LIMIT 1
    """)
    WorkflowTransitionEntity findTransition(@Param("tenantId") Long tenantId,
                                            @Param("fromState") String fromState,
                                            @Param("action") String action);
}