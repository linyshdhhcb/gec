package com.gec.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.model.system.SysUser;

public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> selectPage(IPage<SysUser> page1, String username);
}
