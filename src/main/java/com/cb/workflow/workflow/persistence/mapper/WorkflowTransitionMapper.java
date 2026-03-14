package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.dto.AdminTransitionRuleItem;
import com.cb.workflow.workflow.persistence.entity.WorkflowTransitionEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
        LIMIT 1
    """)
    WorkflowTransitionEntity findTransition(@Param("tenantId") Long tenantId,
                                            @Param("fromState") String fromState,
                                            @Param("action") String action);

    @Select("""
        SELECT
            id                      AS id,
            from_state              AS fromState,
            action                  AS action,
            to_state                AS toState,
            required_role           AS requiredRole,
            next_assignee_user_id   AS nextAssigneeUserId,
            next_assignee_role_code AS nextAssigneeRoleCode,
            is_active               AS isActive
        FROM workflow_transitions
        WHERE tenant_id = #{tenantId}
        ORDER BY id ASC
    """)
    List<AdminTransitionRuleItem> findAllRules(@Param("tenantId") Long tenantId);

    @Insert("""
        INSERT INTO workflow_transitions (
            tenant_id,
            from_state,
            action,
            to_state,
            required_role,
            next_assignee_user_id,
            next_assignee_role_code,
            is_active
        ) VALUES (
            #{tenantId},
            #{fromState},
            #{action},
            #{toState},
            #{requiredRole},
            #{nextAssigneeUserId},
            #{nextAssigneeRoleCode},
            #{isActive}
        )
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRule(WorkflowTransitionEntity entity);

    @Update("""
        UPDATE workflow_transitions
        SET is_active = #{isActive}
        WHERE tenant_id = #{tenantId}
          AND id = #{id}
    """)
    int updateActive(@Param("tenantId") Long tenantId,
                     @Param("id") Long id,
                     @Param("isActive") Boolean isActive);
}