package com.gec.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.model.system.SysUserRole;
import com.gec.system.mapper.SysUserRoleMapper;
import com.gec.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public void removeByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
    }

    @Override
    public void assignRoles(Long userId, Long[] roleIds) {
        // 1. 先删除该用户的所有角色关联
        this.removeByUserId(userId);

        // 2. 再添加新的角色关联
        if (roleIds != null && roleIds.length > 0) {
            List<SysUserRole> userRoleList = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                userRoleList.add(sysUserRole);
            }
            this.saveBatch(userRoleList);
        }
    }
}
