package com.agileboot.orm.query.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.agileboot.orm.query.AbstractQueryConditionGenerator;
import com.agileboot.orm.query.SortQuery;
import com.agileboot.orm.query.TimeRangeQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;

@Data
public class LoginInfoQuery extends AbstractQueryConditionGenerator {

    private String ipaddr;
    private String status;
    private String username;

    private TimeRangeQuery timeRange;
    private SortQuery sortBy;

    @Override
    public QueryWrapper generateQueryWrapper() {
        QueryWrapper<SysLoginInfoXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StrUtil.isNotEmpty(ipaddr), "ip_address", ipaddr)
            .eq(StrUtil.isNotEmpty(status), "status", status)
            .like(StrUtil.isNotEmpty(username), "user_name", username);

        if (timeRange != null) {
            timeRange.addQueryCondition(queryWrapper, "login_time");
        }
        if (sortBy != null) {
            sortBy.addSortByCondition(queryWrapper);
        }
        return queryWrapper;
    }
}
