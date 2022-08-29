package com.agileboot.domain.system.operationLog;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.agileboot.orm.query.AbstractPageQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperationLogQuery extends AbstractPageQuery {

    private String businessType;
    private String status;
    private String operatorName;
    private String requestModule;


    @Override
    public QueryWrapper toQueryWrapper() {
        QueryWrapper<SysLoginInfoXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(businessType!=null, "business_type", businessType)
            .eq(status != null, "status", status)
            .like(StrUtil.isNotEmpty(operatorName), "user_name", operatorName)
            .like(StrUtil.isNotEmpty(requestModule), "request_module", requestModule);

        addSortCondition(queryWrapper);
        addTimeCondition(queryWrapper, "operation_time");

        return queryWrapper;
    }
}
