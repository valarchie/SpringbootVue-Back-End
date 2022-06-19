package com.ruoyi.system.domain.test.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.test.sys.po.SysPostXEntity;
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
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

}
