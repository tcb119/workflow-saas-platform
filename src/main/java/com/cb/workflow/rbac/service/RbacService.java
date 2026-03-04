package com.cb.workflow.rbac.service;

import com.cb.workflow.rbac.persistence.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RbacService {

    private final UserRoleMapper userRoleMapper;

    public RbacService(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    public List<String> getRoleCodes(Long tenantId, Long userId) {
        return userRoleMapper.selectRoleCodes(tenantId, userId);
    }
}