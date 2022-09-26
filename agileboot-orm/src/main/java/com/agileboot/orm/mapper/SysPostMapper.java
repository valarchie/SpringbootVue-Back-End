package com.agileboot.orm.mapper;

import com.agileboot.orm.entity.SysPostEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 岗位信息表 Mapper 接口
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
public interface SysPostMapper extends BaseMapper<SysPostEntity> {

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Select("SELECT  "
        + "  p.post_id  "
        + "FROM  "
        + "  sys_post p  "
        + "LEFT JOIN sys_user u ON p.post_id = u.post_id  "
        + "WHERE  "
        + "  u.user_id = #{userId} and p.deleted = 0 ")
    List<Long> selectPostListByUserId(Long userId);


}
