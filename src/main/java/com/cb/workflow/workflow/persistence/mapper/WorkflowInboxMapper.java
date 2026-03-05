package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.dto.InboxItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WorkflowInboxMapper {

    // inbox query（待辦箱查詢）：先做 owner inbox（擁有者）
    @Select("""
        SELECT
          wi.id            AS instanceId,
          wi.state         AS state,
          CONCAT('WF#', wi.id) AS title,
          wi.updated_at    AS updatedAt
        FROM workflow_instances wi
        WHERE wi.tenant_id = #{tenantId}
          AND wi.owner_id  = #{userId}
        ORDER BY wi.updated_at DESC
        LIMIT #{limit}
    """)
    List<InboxItem> listOwnerInbox(@Param("tenantId") Long tenantId,
                                   @Param("userId") Long userId,
                                   @Param("limit") int limit);
}