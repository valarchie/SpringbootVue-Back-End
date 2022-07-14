package com.agileboot.admin.controller.system;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.admin.deprecated.entity.SysMenu;
import com.agileboot.admin.response.TreeSelectedDTO;
import com.agileboot.common.annotation.AccessLog;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.AuthenticationUtils;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.domain.system.menu.MenuApplicationService;
import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.query.system.MenuQuery;
import com.agileboot.orm.service.ISysMenuXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private ISysMenuXService menuService;

    @Autowired MenuApplicationService menuApplicationService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public ResponseDTO<List> list(SysMenu menu) {

        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(menu.getStatus() != null, "status", menu.getStatus())
            .like(StrUtil.isNotEmpty(menu.getMenuName()), "menu_name", menu.getMenuName());

        List<SysMenuXEntity> list = menuService.list(queryWrapper);
        return ResponseDTO.ok(list);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public ResponseDTO getInfo(@PathVariable Long menuId) {
        menuService.getById(menuId);
        return ResponseDTO.ok();
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public ResponseDTO treeselect(MenuQuery query) {

        LoginUser loginUser = AuthenticationUtils.getLoginUser();

        List<SysMenuXEntity> sysMenuEntities;

        if(loginUser.isAdmin()) {
            sysMenuEntities = menuService.list(query.generateQueryWrapper());
        } else {
            sysMenuEntities = menuService.selectMenuListByUserId(getUserId());
        }
        List<Tree<Long>> trees = menuApplicationService.buildMenuTreeSelect(sysMenuEntities);

        return ResponseDTO.ok(trees);
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResponseDTO roleMenuTreeSelect(@PathVariable("roleId") Long roleId) {
        List<SysMenuXEntity> menus = menuService.selectMenuListByUserId(getUserId());

        TreeSelectedDTO tree = new TreeSelectedDTO();
        tree.setMenus(menuApplicationService.buildMenuTreeSelect(menus));
        tree.setCheckedKeys(menuService.selectMenuListByRoleId(roleId));

        return ResponseDTO.ok(tree);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @AccessLog(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysMenu menu) {
        if (menuService.checkMenuNameUnique(menu.getMenuName(), null, menu.getParentId())) {
//            return Rdto.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
            return ResponseDTO.fail();
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !HttpUtil.isHttp(menu.getPath())
            && !HttpUtil.isHttps(menu.getPath())) {
//            return Rdto.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
            return ResponseDTO.fail();
        }
        SysMenuXEntity entity = menu.toEntity();
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getUsername());
        entity.insert();

        return ResponseDTO.ok();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @AccessLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysMenu menu) {
        if (menuService.checkMenuNameUnique(menu.getMenuName(), menu.getMenuId(), menu.getParentId())) {
//            return Rdto.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !HttpUtil.isHttp(menu.getPath())
            && !HttpUtil.isHttps(menu.getPath())) {
//            return Rdto.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
            return ResponseDTO.fail();
        }
        if (menu.getMenuId().equals(menu.getParentId())) {
//            return Rdto.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
            return ResponseDTO.fail();
        }

        SysMenuXEntity entity = menu.toEntity();

        entity.setUpdaterId(getUserId());
        entity.setUpdateName(getUsername());
        entity.updateById();
        return ResponseDTO.ok();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @AccessLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public ResponseDTO remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
//            return Rdto.error("存在子菜单,不允许删除");
            return ResponseDTO.fail();
        }
        if (menuService.checkMenuExistRole(menuId)) {
//            return Rdto.error("菜单已分配,不允许删除");
            return ResponseDTO.fail();
        }
        menuService.removeById(menuId);
        return ResponseDTO.ok();
    }


}
