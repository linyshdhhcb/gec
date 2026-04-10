package com.gec.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gec.model.base.BaseEntity;
import lombok.Data;


@Data
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@TableField("user_id")
	private Long userId;

	@TableField("role_id")
	private Long roleId;

}
