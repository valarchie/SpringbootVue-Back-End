package com.agileboot.orm.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.agileboot.orm.entity.SysDeptXEntity;
import com.agileboot.orm.entity.SysRoleXEntity;
import com.agileboot.orm.mapper.SysDeptXMapper;
import com.agileboot.orm.mapper.SysRoleXMapper;
import com.agileboot.orm.service.ISysDeptXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysDeptXServiceImp extends ServiceImpl<SysDeptXMapper, SysDeptXEntity> implements ISysDeptXService {

    @Autowired
    private SysRoleXMapper roleMapper;

    /**
     * 构建前端所需要树结构
     * TODO 这个放在部门类  不是很合适
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<Tree<Long>> buildDeptTree(List<SysDeptXEntity> depts) {

        TreeNodeConfig config = new TreeNodeConfig();
        //默认为id可以不设置
        config.setIdKey("deptId");
        //默认为parentId可以不设置
        config.setParentIdKey("parentId");
        //config.setDeep(4);//最大递归深度
        //config.setWeightKey("priority");//排序字段

        // 3.转树，Tree<>里面泛型为id的类型
        return TreeUtil.build(depts, 0L, config, (dept, tree) -> {
            // 也可以使用 tree.setId(dept.getId());等一些默认值
            tree.putExtra("label", dept.getDeptId());
        });
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRoleXEntity role = roleMapper.selectById(roleId);

        return this.baseMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    @Override
    public boolean checkDeptNameUnique(String deptName, Long deptId, Long parentId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_name", deptName)
            .ne(deptId != null, "dept_id", deptId)
            .eq(parentId != null, "parent_id", parentId);

        SysDeptXEntity one = this.getOne(queryWrapper);
        return one != null;
    }

    @Override
    public boolean checkDeptDataScope(Long deptId) {
        // TODO 防止越权
//        if (!SysUser.isAdmin(AuthenticationUtils.getUserId())) {
//            SysDept dept = new SysDept();
//            dept.setDeptId(deptId);
//            List<SysDept> depts = ((SysDeptServiceImpl) AopContext.currentProxy()).selectDeptList(dept);
//            if (CollUtil.isEmpty(depts)) {
//                throw new ServiceException("没有权限访问部门数据！");
//            }
//        }
        return false;
    }

    @Override
    public boolean existEnabledChildrenDeptById(Long deptId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq( "dept_id", deptId)
            .eq("status", 1)
            .apply( "dept_id = '" + deptId + "' or FIND_IN_SET ( dept_id , ancestors)");
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean hasChildDeptById(Long deptId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptId != null, "parent_id", deptId);
        return baseMapper.exists(queryWrapper);
    }


}
