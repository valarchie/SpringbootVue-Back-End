package com.agileboot.orm.service;

import com.agileboot.orm.po.SysDictTypeXEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 字典类型表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-13
 */
public interface ISysDictTypeXService extends IService<SysDictTypeXEntity> {

    boolean insert(SysDictTypeXEntity dictType);

    boolean update(SysDictTypeXEntity dictType);

}
