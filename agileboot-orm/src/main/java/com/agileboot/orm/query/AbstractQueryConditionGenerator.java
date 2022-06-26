package com.agileboot.orm.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @author valarchie
 */
public abstract class AbstractQueryConditionGenerator<T> {

    public abstract QueryWrapper<T> generateQueryWrapper();

}
