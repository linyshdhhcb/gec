package com.gec.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.model.system.SysUserRole;

public interface SysUserRoleService extends IService<SysUserRole> {

    // 根据用户ID删除角色关联
    void removeByUserId(Long userId);

    // 为用户分配角色
    void assignRoles(Long userId, Long[] roleIds);
}
