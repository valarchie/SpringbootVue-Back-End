package com.agileboot.domain.system.menu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.enums.MenuTypeEnum;
import com.agileboot.orm.mapper.SysMenuXMapper;
import com.agileboot.orm.service.ISysMenuXService;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuApplicationService {

    @Autowired
    private ISysMenuXService menuService;


    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<Tree<Long>> buildMenuTreeSelect(List<SysMenuXEntity> menus) {
        TreeNodeConfig config = new TreeNodeConfig();
        //默认为id可以不设置
        config.setIdKey("menuId");
        return TreeUtil.build(menus, 0L, config, (dept, tree) -> {
            // 也可以使用 tree.setId(dept.getId());等一些默认值
            tree.putExtra("label", dept.getMenuName());
        });
    }


    public List<Tree<Long>> buildMenuEntityTree(Long userId) {
        List<SysMenuXEntity> allMenus = menuService.list();

        List<SysMenuXEntity> menus = null;
        if (AuthenticationUtils.isAdmin(userId)) {
            menus = allMenus;
        } else {
            menus = ((SysMenuXMapper) menuService.getBaseMapper()).selectMenuTreeByUserId(userId);
        }

        TreeNodeConfig config = new TreeNodeConfig();
        //默认为id可以不设置
        config.setIdKey("menuId");
        return TreeUtil.build(menus, 0L, config, (menu, tree) -> {
            // 也可以使用 tree.setId(dept.getId());等一些默认值
            tree.putExtra("entity", menu);
        });

    }


    public List<RouterVo> buildRouterTree(List<Tree<Long>> trees) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        if (CollUtil.isNotEmpty(trees)) {
            for (Tree<Long> tree : trees) {
                RouterVo routerVo = null;
                RouterModel model = (RouterModel)tree.get("entity");
                if(model != null) {

                    if(MenuTypeEnum.DIRECTORY.getValue() == model.getMenuType()) {
                        routerVo = model.produceDirectoryRouterVO(buildRouterTree(tree.getChildren()));
                    }

                    if(model.isMenuFrame()) {
                        routerVo = model.produceMenuFrameRouterVO();
                    }

                    if(model.getParentId() == 0L && model.isInnerLink()) {
                        routerVo = model.produceInnerLinkRouterVO();
                    }

                    routers.add(routerVo);
                }
            }
        }

        return routers;
    }


    public List<RouterVo> getRouterTree(Long userId) {
        List<Tree<Long>> trees = buildMenuEntityTree(userId);
        return buildRouterTree(trees);
    }




}
