package com.agileboot.orm.service;

import com.agileboot.orm.deprecated.domain.SysPost;
import com.agileboot.orm.entity.SysPostXEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 岗位信息表 服务类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface ISysPostXService extends IService<SysPostXEntity> {

    /**
     * 校验岗位名称
     *
     * @param post 岗位信息
     * @return 结果
     */
    boolean checkPostNameUnique(SysPost post);

    /**
     * 校验岗位编码
     *
     * @param post 岗位信息
     * @return 结果
     */
    boolean checkPostCodeUnique(SysPost post);

    /**
     * 根据用户ID获取岗位选择框列表
     * TODO 移到UserService会不会更好
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

}
