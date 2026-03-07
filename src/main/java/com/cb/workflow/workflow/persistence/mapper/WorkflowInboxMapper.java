package com.cb.workflow.workflow.persistence.mapper;

import com.cb.workflow.workflow.dto.InboxItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkflowInboxMapper {

    @Select("""
        <script>
        SELECT
            id                AS instanceId,
            state             AS state,
            owner_user_id     AS ownerId,
            assignee_user_id  AS assigneeUserId,
            assignee_role_code AS assigneeRoleCode,
            updated_at        AS updatedAt
        FROM workflow_instances
        WHERE tenant_id = #{tenantId}
          <if test="state != null and state != ''">
            AND state = #{state}
          </if>
          AND (
               (assignee_user_id IS NOT NULL AND assignee_user_id = #{userId})
               <if test="roleCodes != null and roleCodes.size() > 0">
                 OR (assignee_role_code IS NOT NULL AND assignee_role_code IN
                     <foreach collection="roleCodes" item="rc" open="(" separator="," close=")">
                       #{rc}
                     </foreach>
                 )
               </if>
          )
        ORDER BY updated_at DESC
        LIMIT #{size} OFFSET #{offset}
        </script>
        """)
    List<InboxItem> findInbox(
            @Param("tenantId") Long tenantId,
            @Param("userId") Long userId,
            @Param("roleCodes") List<String> roleCodes,
            @Param("state") String state,
            @Param("size") int size,
            @Param("offset") int offset
    );
}