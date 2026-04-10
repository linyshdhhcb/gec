package com.gec.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.model.system.SysRole;
import com.gec.model.vo.SysRoleQueryVo;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> selectPage(IPage<SysRole> page1, SysRoleQueryVo roleQueryVo);

    // 查询所有角色
    List<SysRole> findAllRoles();

    // 根据用户ID查询角色列表
    List<SysRole> findRolesByUserId(Long userId);
}
