package com.agileboot.orm.service.impl;

import com.agileboot.orm.mapper.SysOperationLogXMapper;
import com.agileboot.orm.po.SysOperationLogXEntity;
import com.agileboot.orm.service.ISysOperationLogXService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志记录 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-08
 */
@Service
public class SysOperationLogXServiceImp extends ServiceImpl<SysOperationLogXMapper, SysOperationLogXEntity> implements
    ISysOperationLogXService {

}
