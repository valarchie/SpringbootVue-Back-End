package com.agileboot.orm.service.impl;

import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.mapper.SysPostXMapper;
import com.agileboot.orm.service.ISysPostXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 岗位信息表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysPostXServiceImp extends ServiceImpl<SysPostXMapper, SysPostXEntity> implements ISysPostXService {

    /**
     * 校验岗位名称是否唯一
     *
     * @param postName 岗位名称
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(Long postId, String postName) {
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(postId != null, "post_id", postId)
            .eq("post_name", postName);
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean checkPostCodeUnique(Long postId, String postCode) {
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(postId != null, "post_id", postId)
            .eq("post_code", postCode);
        return baseMapper.exists(queryWrapper);
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return baseMapper.selectPostListByUserId(userId);
    }


}
