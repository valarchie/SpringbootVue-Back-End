package com.ruoyi.system.domain.test.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.test.sys.mapper.SysDeptXMapper;
import com.ruoyi.system.domain.test.sys.mapper.SysRoleXMapper;
import com.ruoyi.system.domain.test.sys.po.SysDeptXEntity;
import com.ruoyi.system.domain.test.sys.po.SysRoleXEntity;
import com.ruoyi.system.domain.test.sys.service.ISysDeptXService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
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
    public List<SysDeptXEntity> buildDeptTree(List<SysDeptXEntity> depts) {
        List<SysDeptXEntity> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysDeptXEntity dept : depts) {
            tempList.add(dept.getDeptId());
        }
        for (SysDeptXEntity dept : depts) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDeptXEntity> depts) {
        List<SysDeptXEntity> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDeptXEntity> list, SysDeptXEntity t) {
        // 得到子节点列表
        List<SysDeptXEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDeptXEntity tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDeptXEntity> getChildList(List<SysDeptXEntity> list, SysDeptXEntity t) {
        List<SysDeptXEntity> tlist = new ArrayList<>();
        Iterator<SysDeptXEntity> it = list.iterator();
        while (it.hasNext()) {
            SysDeptXEntity n = it.next();
            if (n.getParentId() != null && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDeptXEntity> list, SysDeptXEntity t) {
        return getChildList(list, t).size() > 0;
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRoleXEntity role = roleMapper.selectById(roleId);
        return this.baseMapper.selectDeptListByRoleId(roleId, role.getDeptCheckStrictly());
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("dept_name", dept.getDeptName()).eq("parent_id", dept.getParentId())
            .ne("dept_id", dept.getDeptId());

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
    public long countEnabledChildrenDeptById(Long deptId) {

        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptId != null, "dept_id", deptId)
            .apply(deptId != null, "dept_id = '" + deptId + "' or FIND_IN_SET ( dept_id , ancestors)");

        return this.count(queryWrapper);
    }

    @Override
    public boolean hasChildDeptById(Long deptId) {
        QueryWrapper<SysDeptXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(deptId != null, "parent_id", deptId);

        return this.count(queryWrapper) > 0;
    }

}
