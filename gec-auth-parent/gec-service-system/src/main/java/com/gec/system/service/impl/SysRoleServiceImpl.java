package com.gec.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.model.system.SysRole;
import com.gec.model.system.SysUserRole;
import com.gec.model.vo.SysRoleQueryVo;
import com.gec.system.mapper.SysRoleMapper;
import com.gec.system.mapper.SysUserRoleMapper;
import com.gec.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper,SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<SysRole> selectPage(IPage<SysRole> page1, SysRoleQueryVo roleQueryVo) {
        IPage<SysRole> iPage =   this.baseMapper.selectPage(page1,roleQueryVo);
        return iPage;
    }

    @Override
    public List<SysRole> findAllRoles() {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getIsDeleted, 0);
        return this.list(queryWrapper);
    }

    @Override
    public List<SysRole> findRolesByUserId(Long userId) {
        // 1. 根据用户ID查询用户角色关联表，获取所有角色ID
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        queryWrapper.eq(SysUserRole::getIsDeleted, 0);
        List<SysUserRole> userRoleList = sysUserRoleMapper.selectList(queryWrapper);
        
        // 2. 提取角色ID列表
        List<Long> roleIds = userRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 3. 根据角色ID列表查询角色信息
        if (roleIds.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        
        LambdaQueryWrapper<SysRole> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.in(SysRole::getId, roleIds);
        roleQueryWrapper.eq(SysRole::getIsDeleted, 0);
        return this.list(roleQueryWrapper);
    }
}
