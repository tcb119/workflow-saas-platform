package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.persistence.entity.WorkflowInstanceEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface WorkflowInstanceMapper {

    @Select("""
        SELECT id, tenant_id, owner_user_id, state, version, created_at, updated_at
        FROM workflow_instances
        WHERE tenant_id = #{tenantId} AND id = #{id}
""")
    WorkflowInstanceEntity findByTenantAndId(@Param("tenantId") Long tenantId,
                                             @Param("id") Long id);

    // optimistic locking（樂觀鎖）：WHERE version = #{expectedVersion}
    @Update("""
        UPDATE workflow_instances
        SET state = #{newState},
            version = version + 1,
            updated_at = CURRENT_TIMESTAMP
        WHERE tenant_id = #{tenantId}
          AND id = #{id}
          AND version = #{expectedVersion}
    """)
    int updateStateWithOptimisticLock(@Param("tenantId") Long tenantId,
                                      @Param("id") Long id,
                                      @Param("expectedVersion") Long expectedVersion,
                                      @Param("newState") String newState);
}