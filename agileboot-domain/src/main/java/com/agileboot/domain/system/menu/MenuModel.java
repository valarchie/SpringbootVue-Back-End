package com.agileboot.domain.system.menu;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.orm.entity.SysMenuXEntity;
import com.agileboot.orm.service.ISysMenuXService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MenuModel extends SysMenuXEntity {

    public MenuModel(SysMenuXEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }


    public void checkMenuNameUnique(ISysMenuXService menuService) {
        if (menuService.checkMenuNameUnique(getMenuName(), getMenuId(), getParentId())) {
            throw new ApiException(BusinessErrorCode.MENU_NAME_IS_NOT_UNIQUE);
        }
    }


    public void checkExternalLink() {
        if (getIsExternal() && !HttpUtil.isHttp(getPath()) && !HttpUtil.isHttps(getPath())) {
            throw new ApiException(BusinessErrorCode.MENU_EXTERNAL_LINK_MUST_BE_HTTP);
        }
    }


    public void checkParentId() {
        if (getMenuId().equals(getParentId())) {
            throw new ApiException(BusinessErrorCode.MENU_PARENT_ID_NOT_ALLOW_SELF);
        }
    }

    public void checkHasChildMenus(ISysMenuXService menuService) {
        if (menuService.hasChildByMenuId(getMenuId())) {
            throw new ApiException(BusinessErrorCode.MENU_EXIST_CHILD_MENU_NOT_ALLOW_DELETE);
        }
    }

    public void checkMenuAlreadyAssignToRole(ISysMenuXService menuService) {
        if (menuService.checkMenuExistRole(getMenuId())) {
            throw new ApiException(BusinessErrorCode.MENU_ALREADY_ASSIGN_TO_ROLE_NOT_ALLOW_DELETE);
        }
    }


}
