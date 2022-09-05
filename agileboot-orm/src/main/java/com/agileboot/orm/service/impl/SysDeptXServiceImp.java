package com.agileboot.orm.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import com.agileboot.orm.entity.SysDeptXEntity;
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
        return TreeUtil.build(depts, 0L, (dept, tree) -> {
            tree.setId(dept.getDeptId());
            tree.setParentId(dept.getParentId());
            tree.putExtra("label", dept.getDeptName());
        });
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        return this.baseMapper.selectDeptListByRoleId(roleId);
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
    public boolean existChildrenDeptById(Long deptId, Boolean enabled) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq( "dept_id", deptId)
            .eq(enabled != null, "status", 1)
            .apply( "FIND_IN_SET (dept_id , ancestors)");
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean isChildOfTargetDeptId(Long ancestorId, Long childId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("dept_id = '" + childId + "' or FIND_IN_SET ( " + ancestorId + " , ancestors)");
        return this.baseMapper.exists(queryWrapper);
    }

    @Override
    public boolean hasChildDeptById(Long deptId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptId != null, "parent_id", deptId);
        return baseMapper.exists(queryWrapper);
    }


}
