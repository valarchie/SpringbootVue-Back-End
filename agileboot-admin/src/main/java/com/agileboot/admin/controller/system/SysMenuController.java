package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.constant.UserConstants;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.orm.deprecated.entity.SysMenu;
import com.agileboot.orm.po.SysMenuXEntity;
import com.agileboot.orm.service.ISysMenuXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
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

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public ResponseDTO list(SysMenu menu) {
        Page<SysMenuXEntity> page = getPage();
        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(menu.getStatus() != null, "status", menu.getStatus())
            .like(StrUtil.isNotEmpty(menu.getMenuName()), "menu_name", menu.getMenuName());

        menuService.page(page, queryWrapper);
        return ResponseDTO.success(page.getRecords());
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public ResponseDTO getInfo(@PathVariable Long menuId) {
        return ResponseDTO.success(menuService.getById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public ResponseDTO treeselect(SysMenu menu) {
        List<SysMenuXEntity> menus = menuService.selectMenuList(menu, getUserId());

        List<SysMenu> collect = menus.stream().map(SysMenu::new).collect(Collectors.toList());

        return ResponseDTO.success(menuService.buildMenuTree(collect));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public ResponseDTO roleMenuTreeselect(@PathVariable("roleId") Long roleId) {
        List<SysMenuXEntity> menus = menuService.selectMenuList(getUserId());

        List<SysMenu> collect = menus.stream().map(SysMenu::new).collect(Collectors.toList());
        ResponseDTO ajax = ResponseDTO.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTree(collect));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysMenu menu) {
        if (menuService.checkMenuNameUnique(menu)) {
            return ResponseDTO.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !HttpUtil.isHttp(menu.getPath())
            && !HttpUtil.isHttps(menu.getPath())) {
            return ResponseDTO.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        SysMenuXEntity entity = menu.toEntity();
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getUsername());
        return toAjax(entity.insert());
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysMenu menu) {
        if (menuService.checkMenuNameUnique(menu)) {
            return ResponseDTO.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !HttpUtil.isHttp(menu.getPath())
            && !HttpUtil.isHttps(menu.getPath())) {
            return ResponseDTO.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        if (menu.getMenuId().equals(menu.getParentId())) {
            return ResponseDTO.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }

        SysMenuXEntity entity = menu.toEntity();

        entity.setUpdaterId(getUserId());
        entity.setUpdateName(getUsername());
        return toAjax(entity.updateById());
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public ResponseDTO remove(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId)) {
            return ResponseDTO.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return ResponseDTO.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.removeById(menuId));
    }


}
