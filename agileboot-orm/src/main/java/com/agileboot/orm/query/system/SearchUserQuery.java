package com.agileboot.orm.query.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.query.AbstractQueryConditionGenerator;
import com.agileboot.orm.query.TimeRangeQuery;
import com.agileboot.orm.result.SearchUserResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchUserQuery  extends AbstractQueryConditionGenerator<SearchUserResult> {

    private Long userId;
    private String username;
    private Integer status;
    private String phoneNumber;
    private Long deptId;

    private TimeRangeQuery timeRange;

    @Override
    public QueryWrapper<SearchUserResult> generateQueryWrapper() {
        QueryWrapper<SearchUserResult> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StrUtil.isNotEmpty(username), "username", username)
            .like(StrUtil.isNotEmpty(phoneNumber), "u.phone_number", phoneNumber)
            .eq(userId != null, "u.user_id", userId)
            .eq(status != null, "u.status", status)
            .eq("u.deleted", 0)
            .and(deptId != null, o ->
                o.eq("u.dept_id", deptId)
                    .or()
                    .apply("u.dept_id IN ( SELECT t.dept_id FROM sys_dept t WHERE find_in_set(" + deptId
                        + ", ancestors))"));

        if (timeRange != null) {
            timeRange.addQueryCondition(queryWrapper, "create_time");
        }

        return queryWrapper;
    }
}
