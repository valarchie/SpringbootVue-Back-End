package com.agileboot.orm.service;

import com.agileboot.orm.po.SysConfigXEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 参数配置表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-09
 */
public interface ISysConfigXService extends IService<SysConfigXEntity> {

    String getConfigValueByKey(String key);

    boolean isCaptchaOn();

}
