package com.agileboot.domain.system.menu;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.query.AbstractQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Arrays;
import lombok.Data;

@Data
public class MenuQuery extends AbstractQuery {

    private String menuName;
    private Boolean isVisible;
    private Integer status;


    @SuppressWarnings("rawtypes")
    @Override
    public QueryWrapper<SysMenuXEntity> toQueryWrapper() {

        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotEmpty(menuName), columnName("menu_name"), menuName)
            .eq(isVisible!=null, "is_visible", isVisible)
            .eq(status!=null, "status", status);

        queryWrapper.orderBy(true, true, Arrays.asList("m.parent_id", "order_num"));
        return queryWrapper;
    }
}
