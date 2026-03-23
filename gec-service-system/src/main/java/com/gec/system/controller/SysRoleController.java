package com.gec.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.model.system.SysRole;
import com.gec.model.vo.SysRoleQueryVo;
import com.gec.system.config.Result;
import com.gec.system.service.SysRoleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    // http://localhost:8080/admin/system/sysRole/findAll

    // http://localhost:8080/admin/system/sysRole/findAll

    // 查询全部记录
    @ApiOperation("查询全部接口")
    @GetMapping("/findAll")
    public List<SysRole> findAll()
    {
        List<SysRole> list = this.sysRoleService.list();
        return list;
    }

    // 根据id 去逻辑删除
    @ApiOperation("逻辑删除接口")
    @DeleteMapping("/remove/{id}")
    public boolean removeRole(@PathVariable Long id)
    {
        boolean isSuccess = this.sysRoleService.removeById(id);
        return isSuccess;
    }

    @ApiOperation("角色分页查询")
    @GetMapping("/{page}/{limit}")
    public Result findRoleByPageQuery(@PathVariable Long page,
                                      @PathVariable Long limit,
                                      SysRoleQueryVo roleQueryVo)
    {
        IPage<SysRole> page1 = new Page<>(page,limit);
        page1 =   this.sysRoleService.selectPage(page1,roleQueryVo);
        return Result.ok(page1);
    }
}
