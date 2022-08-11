package com.agileboot.orm.service.impl;

import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.entity.SysRoleMenuXEntity;
import com.agileboot.orm.enums.MenuTypeEnum;
import com.agileboot.orm.mapper.SysMenuXMapper;
import com.agileboot.orm.mapper.SysRoleMenuXMapper;
import com.agileboot.orm.mapper.SysRoleXMapper;
import com.agileboot.orm.service.ISysMenuXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
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
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return this.baseMapper.selectMenuListByRoleId(roleId);
    }

    @Override
    public boolean checkMenuNameUnique(String menuName, Long menuId, Long parentId) {
        QueryWrapper<SysMenuXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_name", menuName)
            .ne(menuId!=null, "menu_id", menuId)
            .eq(parentId != null, "parent_id", parentId);
        return this.baseMapper.exists(queryWrapper);
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
    public List<SysMenuXEntity> listMenuListWithoutButtonByUserId(Long userId) {
        List<SysMenuXEntity> sysMenuList = this.baseMapper.selectMenuListByUserId(userId);

        if (sysMenuList == null) {
            return null;
        }

        return sysMenuList.stream()
            .filter(menu -> menu.getMenuType() != MenuTypeEnum.BUTTON.getValue())
            .collect(Collectors.toList());
    }

    @Override
    public List<SysMenuXEntity> listMenuListWithoutButton() {
        List<SysMenuXEntity> sysMenuList = this.list();

        if (sysMenuList == null) {
            return null;
        }

        return sysMenuList.stream()
            .filter(menu -> menu.getMenuType() != MenuTypeEnum.BUTTON.getValue())
            .collect(Collectors.toList());
    }

    @Override
    public List<SysMenuXEntity> selectMenuListByUserId(Long userId) {
        return baseMapper.selectMenuListByUserId(userId);
    }





}
