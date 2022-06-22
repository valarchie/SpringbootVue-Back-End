package com.springvue.orm.domain.entity;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.core.domain.BaseEntity;
import com.springvue.orm.domain.test.sys.po.SysMenuXEntity;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SysMenu extends BaseEntity {

    public SysMenu(SysMenuXEntity entity) {
        this.menuId = entity.getMenuId();
        this.menuName = entity.getMenuName();
        this.parentId = entity.getParentId();
        this.orderNum = entity.getOrderNum();
        this.path = entity.getPath();
        this.component = entity.getComponent();
        this.query = entity.getQuery();
        this.isCache = entity.getIsCache()+"";
        this.isFrame = entity.getIsFrame() +"";
        this.menuType = transferMenuType(entity.getMenuType());
        this.visible = entity.getIsVisible()+"";
        this.setCreateBy(entity.getCreatorName());
        this.setCreateTime(entity.getCreateTime());
    }

    public SysMenuXEntity toEntity() {

        SysMenuXEntity entity = new SysMenuXEntity();

        entity.setMenuId(this.menuId);
        entity.setMenuName(this.menuName);
        entity.setParentId(this.parentId);
        entity.setOrderNum(this.orderNum);
        entity.setPath(this.path);
        entity.setComponent(this.component);
        entity.setQuery(this.query);
        entity.setIsCache(Convert.toBool(this.isCache));
        entity.setIsFrame(Convert.toBool(this.isFrame));
        entity.setMenuType(transferMenuType(this.menuType));
        entity.setIsVisible(Convert.toBool(this.visible));

        return entity;
    }

    public int transferMenuType(String typeStr) {
        int type = 0;
        switch (typeStr) {
            case "M": type = 1;break;
            case"C": type = 2;break;
            case "F": type = 3;break;
            default:type = 0;
        }
        return type;
    }



    public String transferMenuType(Integer type) {
        String typeStr = "";

        switch (type) {
            case 1: typeStr = "M";break;
            case 2: typeStr = "C";break;
            case 3: typeStr = "F";break;
            default:typeStr = "Unknow";
        }
        return typeStr;
    }



    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 父菜单名称
     */
    private String parentName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /**
     * 路由地址
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否为外链（0是 1否）
     */
    private String isFrame;

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    private String isCache;

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /**
     * 显示状态（0显示 1隐藏）
     */
    private String visible;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private String status;

    /**
     * 权限字符串
     */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 子菜单
     */
    private List<SysMenu> children = new ArrayList<SysMenu>();

}
