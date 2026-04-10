package com.gec.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.model.system.SysUser;
import com.gec.system.mapper.SysUserMapper;
import com.gec.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public IPage<SysUser> selectPage(IPage<SysUser> page1, String username) {
        IPage<SysUser> iPage = this.baseMapper.selectPage(page1, username);
        return iPage;
    }
}
