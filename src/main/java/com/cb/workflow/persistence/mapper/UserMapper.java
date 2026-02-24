package com.cb.workflow.persistence.mapper;

import com.cb.workflow.persistence.entity.UserEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("""
        SELECT id,
               tenant_id AS tenantId,
               email,
               password_hash AS passwordHash,
               status,
               created_at AS createdAt,
               updated_at AS updatedAt
        FROM users
        WHERE tenant_id = #{tenantId}
          AND email = #{email}
        LIMIT 1
    """)
    UserEntity findByTenantAndEmail(@Param("tenantId") Long tenantId,
                                    @Param("email") String email);

    @Select("""
        SELECT id,
               tenant_id AS tenantId,
               email,
               password_hash AS passwordHash,
               status,
               created_at AS createdAt,
               updated_at AS updatedAt
        FROM users
        WHERE tenant_id = #{tenantId}
          AND id = #{id}
        LIMIT 1
    """)
    UserEntity findByTenantAndId(@Param("tenantId") Long tenantId,
                                 @Param("id") Long id);

    @Insert("""
        INSERT INTO users(tenant_id, email, password_hash, status)
        VALUES(#{tenantId}, #{email}, #{passwordHash}, #{status})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserEntity user);
}