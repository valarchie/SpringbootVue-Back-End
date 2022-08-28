package com.agileboot.orm.query;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

@Data
@Deprecated
public class SortQuery {

    private static final String ASC = "ascending";
    private static final String DESC = "descending";

    private String column;
    private String direction;

    public void addQueryCondition(QueryWrapper<?> queryWrapper) {
        if(queryWrapper != null) {
            queryWrapper.orderBy(StrUtil.isNotBlank(column), convertSortDirection(),
                StrUtil.toUnderlineCase(column));
        }
    }

    public boolean convertSortDirection() {
        boolean orderDirection = true;
        if (StrUtil.isNotEmpty(direction)) {
            // 兼容前端排序类型
            if (ASC.equals(direction)) {
                orderDirection = true;
            } else if (DESC.equals(direction)) {
                orderDirection = false;
            }
        }
        return orderDirection;
    }

}
