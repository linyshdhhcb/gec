package com.gec.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gec.model.base.BaseEntity;
import lombok.Data;


@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@TableField("username")
	private String username;

	@TableField("password")
	private String password;

	@TableField("name")
	private String name;

	@TableField("phone")
	private String phone;

	@TableField("email")
	private String email;

	@TableField("avatar")
	private String avatar;

	@TableField("gender")
	private Integer gender;

	@TableField("status")
	private Integer status;

}
