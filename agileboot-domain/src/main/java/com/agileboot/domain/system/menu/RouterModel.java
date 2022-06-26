package com.agileboot.domain.system.menu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.enums.MenuComponentEnum;
import com.agileboot.orm.enums.MenuTypeEnum;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class RouterModel extends SysMenuXEntity {


    public RouterVo produceDirectoryRouterVO(List<RouterVo> children) {

        RouterVo router = new RouterVo();
        router.setHidden(getIsVisible());
        router.setName(getRouteName());
        router.setPath(getRouterPath());
        router.setComponent(getComponent());
        router.setQuery(getQuery());
        router.setMeta(new MetaVo(getMenuName(), getIcon(), getIsCache(), getPath()));

        if (CollUtil.isNotEmpty(children) && MenuTypeEnum.DIRECTORY.getValue() == getMenuType()) {
            router.setAlwaysShow(true);
            router.setRedirect("noRedirect");
            router.setChildren(children);
        }

        return router;
    }


    public RouterVo produceMenuFrameRouterVO() {
        RouterVo router = new RouterVo();

        router.setMeta(null);
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        children.setPath(getPath());
        children.setComponent(getComponent());
        children.setName(StrUtil.upperFirst(getPath()));
        children.setMeta(new MetaVo(getMenuName(), getIcon(), getIsCache(),getPath()));
        children.setQuery(getQuery());
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }


    public RouterVo produceInnerLinkRouterVO() {

        RouterVo router = new RouterVo();

        router.setMeta(new MetaVo(getMenuName(), getIcon()));
        router.setPath("/");
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        String routerPath = innerLinkReplaceEach(getPath());
        children.setPath(routerPath);
        children.setComponent(UserConstants.INNER_LINK);
        children.setName(StrUtil.upperFirst(routerPath));
        children.setMeta(new MetaVo(getMenuName(), getIcon(),getPath()));
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }


    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName() {
        String routerName = StrUtil.upperFirst(getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame()) {
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
    public boolean isMenuFrame() {
        return getParentId().intValue() == 0
            && MenuTypeEnum.MENU.getValue() == getMenuType()
            && getIsFrame();
    }


    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath() {
        String routerPath = getPath();
        // 内链打开外网方式
        if (getParentId().intValue() != 0 && isInnerLink()) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == getParentId()
            && MenuTypeEnum.DIRECTORY.getValue() == getMenuType()
            && getIsFrame()) {
            routerPath = "/" + getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame()) {
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
    public boolean isInnerLink() {
        return getIsFrame() &&
            (HttpUtil.isHttp(getPath()) || HttpUtil.isHttps(getPath()));
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
    public String getMenuComponent() {
        String component = MenuComponentEnum.LAYOUT.getDescription();
        if (StrUtil.isNotEmpty(getComponent()) && !isMenuFrame()) {
            component = getComponent();
        } else if (StrUtil.isEmpty(getComponent()) && getParentId().intValue() != 0 && isInnerLink()) {
            component = MenuComponentEnum.INNER_LINK.getDescription();
        } else if (StrUtil.isEmpty(getComponent()) && isParentView()) {
            component = MenuComponentEnum.PARENT_VIEW.getDescription();
        }
        return component;
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView() {
        return getParentId().intValue() != 0 && MenuTypeEnum.DIRECTORY.getValue() == getMenuType();
    }




}
