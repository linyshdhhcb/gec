package com.gec.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.model.system.SysRole;
import com.gec.model.vo.SysRoleQueryVo;
import com.gec.system.config.Result;
import com.gec.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping(value = "/admin/system/role")
@CrossOrigin
public class RoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result findRoleByPageQuery(
           // @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

          //  @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

           // @ApiParam(name = "roleQueryVo", value = "查询对象", required = false)
                    SysRoleQueryVo roleQueryVo) {
        Page<SysRole> pageParam = new Page<SysRole>(page, limit);
        IPage<SysRole> pageModel = sysRoleService.selectPage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("removeRoleById/{id}")
    public Result removeRoleById(@PathVariable Long id){
        sysRoleService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "添加角色")
    @PostMapping("addRole")
    public Result addRole(@RequestBody SysRole sysRole){
        sysRoleService.save(sysRole);
        return Result.ok();
    }

    @ApiOperation(value = "根据 id 查询角色")
    @GetMapping("findRoleById/{id}")
    public Result findRoleById(@PathVariable Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    @ApiOperation(value = "修改角色")
    @PostMapping("updateRole")
    public Result updateRole(@RequestBody SysRole sysRole){
        sysRoleService.updateById(sysRole);
        return Result.ok();
    }

    @ApiOperation(value = "批量删除角色")
    @DeleteMapping("deleteBatchRoleByIds")
    public Result deleteBatchRoleByIds(@RequestBody List<Long> ids){
        sysRoleService.removeByIds(ids);
        return Result.ok();
    }
}
