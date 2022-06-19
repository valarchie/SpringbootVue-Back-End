package com.ruoyi.system.domain.test.sys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.test.sys.mapper.SysMenuXMapper;
import com.ruoyi.system.domain.test.sys.mapper.SysRoleMenuXMapper;
import com.ruoyi.system.domain.test.sys.mapper.SysRoleXMapper;
import com.ruoyi.system.domain.test.sys.po.SysMenuXEntity;
import com.ruoyi.system.domain.test.sys.po.SysRoleMenuXEntity;
import com.ruoyi.system.domain.test.sys.po.SysRoleXEntity;
import com.ruoyi.system.domain.test.sys.service.ISysMenuXService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author valarchie
 * @since 2022-06-16
 */
@Service
public class SysMenuXServiceImp extends ServiceImpl<SysMenuXMapper, SysMenuXEntity> implements ISysMenuXService {

    @Autowired
    private SysRoleXMapper roleMapper;
    @Autowired
    private SysRoleMenuXMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuXEntity> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    @Override
    public List<SysMenuXEntity> selectMenuList(SysMenu menu, Long userId) {

        List<SysMenuXEntity> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId)) {

            QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(StrUtil.isNotEmpty(menu.getMenuName()), "menu_name", menu.getMenuName())
                .eq(menu.getVisible() != null, "is_visible", menu.getVisible())
                .eq(menu.getStatus() != null, "status", menu.getStatus());

            menuList = this.baseMapper.selectList(queryWrapper);
        } else {
            menu.getParams().put("userId", userId);
            menuList = this.baseMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysMenu dept : menus) {
            tempList.add(dept.getMenuId());
        }
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRoleXEntity sysRoleXEntity = roleMapper.selectById(roleId);
        return this.baseMapper.selectMenuListByRoleId(roleId, sysRoleXEntity.getMenuCheckStrictly());
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {

        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_name", menu.getMenuName())
            .eq(menu.getParentId() != null, "parent_id", menu.getParentId());

        SysMenuXEntity one = this.getOne(queryWrapper);

        return one!=null && Objects.equals(one.getMenuId(), menu.getMenuId());
    }


    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }


    @Override
    public boolean hasChildByMenuId(Long menuId) {

        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", menuId);

        return this.count(queryWrapper) > 0;
    }
    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        QueryWrapper<SysRoleMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_id", menuId);
        return roleMenuMapper.selectCount(queryWrapper) > 0;
    }

}
