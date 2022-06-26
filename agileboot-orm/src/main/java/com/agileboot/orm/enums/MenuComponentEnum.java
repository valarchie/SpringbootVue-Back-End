package com.agileboot.orm.enums;

public enum MenuComponentEnum {

    /**
     * 菜单组件类型
     */
    LAYOUT("Layout"),
    PARENT_VIEW("ParentView"),
    INNER_LINK("InnerLink");

    private final String description;

    MenuComponentEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
