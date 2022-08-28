package com.agileboot.orm.query;

import com.agileboot.common.utils.time.DatePicker;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Date;
import lombok.Data;

/**
 * @author valarchie
 */
@Data
@Deprecated
public class TimeRangeQuery {

    private Date beginTime;
    private Date endTime;

    @SuppressWarnings("unchecked")
    public void addQueryCondition(QueryWrapper queryWrapper, String fieldName) {
        if(queryWrapper!=null) {
            queryWrapper
                .ge(beginTime != null, fieldName, DatePicker.getBeginOfTheDay(beginTime))
                .le(endTime != null, fieldName, DatePicker.getEndOfTheDay(endTime));
        }
    }

}
