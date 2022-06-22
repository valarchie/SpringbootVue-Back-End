package com.agileboot.orm.service.impl;

import com.agileboot.orm.mapper.SysDictTypeXMapper;
import com.agileboot.orm.po.SysDictTypeXEntity;
import com.agileboot.orm.service.ISysDictTypeXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 字典类型表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-13
 */
@Service
public class SysDictTypeXServiceImp extends ServiceImpl<SysDictTypeXMapper, SysDictTypeXEntity> implements
    ISysDictTypeXService {

    @Override
    public boolean insert(SysDictTypeXEntity dictType) {
        QueryWrapper<SysDictTypeXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", dictType.getDictType());
        SysDictTypeXEntity one = this.getOne(queryWrapper);
        if (one != null) {
            throw new RuntimeException("duplicated dict type!!");
        }
        return dictType.insert();
    }

    @Override
    public boolean update(SysDictTypeXEntity dictType) {
        QueryWrapper<SysDictTypeXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", dictType.getDictType());
        SysDictTypeXEntity one = this.getOne(queryWrapper);
        if (one != null) {
            throw new RuntimeException("duplicated dict type!!");
        }
        queryWrapper.eq("dict_type", dictType.getDictId());
        return dictType.update(queryWrapper);
    }
}
