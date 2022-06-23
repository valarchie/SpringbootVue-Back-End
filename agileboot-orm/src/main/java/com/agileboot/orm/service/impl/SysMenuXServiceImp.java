package com.agileboot.orm.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.orm.deprecated.entity.SysMenu;
import com.agileboot.orm.deprecated.entity.SysUser;
import com.agileboot.orm.deprecated.vo.MetaVo;
import com.agileboot.orm.deprecated.vo.RouterVo;
import com.agileboot.orm.mapper.SysMenuXMapper;
import com.agileboot.orm.mapper.SysRoleMenuXMapper;
import com.agileboot.orm.mapper.SysRoleXMapper;
import com.agileboot.orm.po.SysMenuXEntity;
import com.agileboot.orm.po.SysRoleMenuXEntity;
import com.agileboot.orm.po.SysRoleXEntity;
import com.agileboot.orm.service.ISysMenuXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {

        List<SysMenuXEntity> allMenus = this.list();

        List<SysMenuXEntity> menus = null;
        if (AuthenticationUtils.isAdmin(userId)) {
            menus = allMenus;
        } else {
            menus = baseMapper.selectMenuTreeByUserId(userId);
        }

        List<SysMenu> sysMenuModels = menus.stream().map(SysMenu::new).collect(Collectors.toList());

        // TODO 优化  内层可以换做SysMenuModel
        Map<Long, List<SysMenuXEntity>> groupByParentId = allMenus.stream()
            .collect(Collectors.groupingBy(SysMenuXEntity::getParentId));

        for (SysMenu menu : sysMenuModels) {
            if(groupByParentId.containsKey(menu.getMenuId())) {
                List<SysMenuXEntity> sysMenuXEntities = groupByParentId.get(menu.getMenuId());
                List<SysMenu> children = sysMenuXEntities.stream().map(SysMenu::new).collect(Collectors.toList());
                menu.setChildren(children);
            }
        }

        return sysMenuModels;
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(
                new MetaVo(menu.getMenuName(), menu.getIcon(), StrUtil.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StrUtil.upperFirst(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StrUtil.equals("1", menu.getIsCache()),
                    menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StrUtil.upperFirst(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }


    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StrUtil.upperFirst(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StrUtil.EMPTY;
        }
        return routerName;
    }


    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
            && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }


    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
            && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

     /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) &&
            (HttpUtil.isHttp(menu.getPath()) || HttpUtil.isHttps(menu.getPath()));
    }


    /**
     * 内链域名特殊字符替换
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS},
            new String[]{"", ""});
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StrUtil.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StrUtil.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StrUtil.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

}
