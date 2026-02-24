package com.cb.workflow.persistence.mapper;

import org.apache.ibatis.annotations.*;

import java.time.OffsetDateTime;

@Mapper
public interface RefreshTokenMapper {

    @Insert("""
        INSERT INTO refresh_tokens(tenant_id, user_id, token_hash, expires_at)
        VALUES(#{tenantId}, #{userId}, #{tokenHash}, #{expiresAt})
    """)
    int insert(@Param("tenantId") Long tenantId,
               @Param("userId") Long userId,
               @Param("tokenHash") String tokenHash,
               @Param("expiresAt") OffsetDateTime expiresAt);

    @Select("""
        SELECT user_id
        FROM refresh_tokens
        WHERE tenant_id = #{tenantId}
          AND token_hash = #{tokenHash}
          AND revoked_at IS NULL
          AND expires_at > NOW()
        LIMIT 1
    """)
    Long findActiveUserId(@Param("tenantId") Long tenantId,
                          @Param("tokenHash") String tokenHash);

    @Update("""
        UPDATE refresh_tokens
        SET revoked_at = NOW()
        WHERE tenant_id = #{tenantId}
          AND token_hash = #{tokenHash}
          AND revoked_at IS NULL
    """)
    int revoke(@Param("tenantId") Long tenantId,
               @Param("tokenHash") String tokenHash);
}