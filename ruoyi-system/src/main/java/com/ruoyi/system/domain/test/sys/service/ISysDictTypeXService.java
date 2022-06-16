package com.ruoyi.system.domain.test.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.test.sys.po.SysDictTypeXEntity;

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
