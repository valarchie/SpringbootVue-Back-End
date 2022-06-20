package com.springvue.orm.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.SysPost;
import com.springvue.orm.domain.test.sys.mapper.SysPostXMapper;
import com.springvue.orm.domain.test.sys.po.SysPostXEntity;
import com.springvue.orm.domain.test.sys.service.ISysPostXService;
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
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post) {
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(post.getPostId() != null, "post_id", post.getPostId())
            .eq("post_name", post.getPostName());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(post.getPostId() != null, "post_id", post.getPostId())
            .eq("post_code", post.getPostCode());
        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return baseMapper.selectPostListByUserId(userId);
    }


}
