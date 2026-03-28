package com.gec.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.model.system.SysRole;
import com.gec.model.vo.SysRoleQueryVo;
import com.gec.system.config.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "角色管理接口")
@RestController
@RequestMapping(value = "/admin/system/role")
@CrossOrigin
public class RoleController {
    @Autowired
    private IRoleService iRoleService;

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
        IPage<SysRole> pageModel = iRoleService.selectPage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }
}
