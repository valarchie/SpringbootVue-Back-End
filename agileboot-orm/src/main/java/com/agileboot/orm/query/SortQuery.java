package com.agileboot.orm.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

@Data
public class SortQuery<T> {

    private String column;
    private String direction;

    @SuppressWarnings("rawtypes")
    public QueryWrapper<T> addSortByCondition(QueryWrapper<T> queryWrapper) {

        if(queryWrapper != null) {
            queryWrapper.orderBy(StrUtil.isNotBlank(column), convertSortDirection(),
                StrUtil.toUnderlineCase(column));
        }

        return queryWrapper;
    }

    public boolean convertSortDirection() {
        boolean orderDirection = true;
        if (StrUtil.isNotEmpty(direction)) {
            // 兼容前端排序类型
            if ("ascending".equals(direction)) {
                orderDirection = true;
            } else if ("descending".equals(direction)) {
                orderDirection = false;
            }
        }
        return orderDirection;
    }

}
