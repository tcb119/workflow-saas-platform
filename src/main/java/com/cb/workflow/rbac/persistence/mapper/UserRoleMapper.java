package com.cb.workflow.rbac.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    @Select("""
        SELECT r.code
        FROM user_roles ur
        JOIN roles r ON r.id = ur.role_id
        WHERE ur.tenant_id = #{tenantId}
          AND ur.user_id = #{userId}
          AND r.tenant_id = #{tenantId}
    """)
    List<String> selectRoleCodes(@Param("tenantId") Long tenantId,
                                 @Param("userId") Long userId);
}