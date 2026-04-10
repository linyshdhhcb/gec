package com.gec.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.model.system.SysRole;
import com.gec.model.system.SysUser;
import com.gec.system.config.Result;
import com.gec.system.service.SysRoleService;
import com.gec.system.service.SysUserRoleService;
import com.gec.system.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @ApiOperation("查询全部用户接口")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysUser> list = this.sysUserService.list();
        return Result.ok(list);
    }

    @ApiOperation("逻辑删除用户接口")
    @DeleteMapping("/remove/{id}")
    public Result removeUser(@PathVariable Long id) {
        boolean isSuccess = this.sysUserService.removeById(id);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("用户分页查询")
    @GetMapping("/{page}/{limit}")
    public Result findUserByPageQuery(@PathVariable Long page,
                                      @PathVariable Long limit,
                                      @RequestParam(required = false) String username) {
        IPage<SysUser> page1 = new Page<>(page, limit);
        page1 = this.sysUserService.selectPage(page1, username);
        return Result.ok(page1);
    }

    @ApiOperation("添加用户")
    @PostMapping("/save")
    public Result saveUser(@RequestBody SysUser sysUser) {
        boolean isSuccess = this.sysUserService.save(sysUser);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("根据id查询用户")
    @GetMapping("/get/{id}")
    public Result getUserById(@PathVariable Long id) {
        SysUser sysUser = this.sysUserService.getById(id);
        return Result.ok(sysUser);
    }

    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result updateUser(@RequestBody SysUser sysUser) {
        boolean isSuccess = this.sysUserService.updateById(sysUser);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("批量删除用户")
    @DeleteMapping("/batchRemove")
    public Result batchRemoveUsers(@RequestBody List<Long> ids) {
        boolean isSuccess = this.sysUserService.removeByIds(ids);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("获取所有角色列表")
    @GetMapping("/findAllRoles")
    public Result findAllRoles() {
        List<SysRole> roleList = this.sysRoleService.findAllRoles();
        return Result.ok(roleList);
    }

    @ApiOperation("根据用户ID获取角色列表")
    @GetMapping("/findRolesByUserId/{userId}")
    public Result findRolesByUserId(@PathVariable Long userId) {
        List<SysRole> roleList = this.sysRoleService.findRolesByUserId(userId);
        return Result.ok(roleList);
    }

    @ApiOperation("为用户分配角色")
    @PostMapping("/assignRoles")
    public Result assignRoles(@RequestParam Long userId, @RequestBody Long[] roleIds) {
        this.sysUserRoleService.assignRoles(userId, roleIds);
        return Result.ok();
    }
}
