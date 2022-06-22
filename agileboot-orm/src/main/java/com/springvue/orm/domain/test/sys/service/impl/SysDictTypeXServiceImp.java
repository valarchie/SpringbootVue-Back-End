package com.springvue.orm.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.test.sys.mapper.SysDictTypeXMapper;
import com.springvue.orm.domain.test.sys.po.SysDictTypeXEntity;
import com.springvue.orm.domain.test.sys.service.ISysDictTypeXService;
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
