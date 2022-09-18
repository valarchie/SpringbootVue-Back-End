package com.agileboot.domain.system.role;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysUserXEntity;
import com.agileboot.orm.query.AbstractPageQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

/**
 * @author valarchie
 */
@Data
public class AllocatedRoleQuery extends AbstractPageQuery {

    private Long roleId;
    private String username;
    private String phoneNumber;

    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<SysUserXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId).like(StrUtil.isNotEmpty(username),"u.username", username)
            .like(StrUtil.isNotEmpty(phoneNumber), "u.phone_number", phoneNumber);

        return queryWrapper;
    }
}
