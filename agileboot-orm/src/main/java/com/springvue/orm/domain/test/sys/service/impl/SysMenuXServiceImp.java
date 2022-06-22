package com.springvue.orm.domain.test.sys.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.springvue.orm.domain.entity.SysMenu;
import com.springvue.orm.domain.entity.SysUser;
import com.springvue.orm.domain.test.sys.mapper.SysMenuXMapper;
import com.springvue.orm.domain.test.sys.mapper.SysRoleMenuXMapper;
import com.springvue.orm.domain.test.sys.mapper.SysRoleXMapper;
import com.springvue.orm.domain.test.sys.po.SysMenuXEntity;
import com.springvue.orm.domain.test.sys.po.SysRoleMenuXEntity;
import com.springvue.orm.domain.test.sys.po.SysRoleXEntity;
import com.springvue.orm.domain.test.sys.service.ISysMenuXService;
import java.util.List;
import java.util.Objects;
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
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<Tree<Long>> buildMenuTree(List<SysMenu> menus) {

        TreeNodeConfig config = new TreeNodeConfig();
        //默认为id可以不设置
        config.setIdKey("menuId");
        return TreeUtil.build(menus, 0L, config, (dept, tree) -> {
            // 也可以使用 tree.setId(dept.getId());等一些默认值
            tree.putExtra("label", dept.getMenuName());
        });
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



    @Override
    public boolean hasChildByMenuId(Long menuId) {
        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", menuId);

        return baseMapper.exists(queryWrapper);
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
        return roleMenuMapper.exists(queryWrapper);
    }

}
