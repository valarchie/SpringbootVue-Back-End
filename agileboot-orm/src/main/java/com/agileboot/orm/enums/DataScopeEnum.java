package com.agileboot.orm.enums;

/**
 * 对应sys_role表的data_scope字段
 * @author valarchie
 */
public enum DataScopeEnum {

    /**
     * 数据权限范围
     */
    ALL(1, "所有数据权限"),
    SELF_DEFINE(2, "自定义数据权限"),
    CURRENT_DEPT_AND_CHILDREN_DEPT(3, "本部门以及子孙部门数据权限");

    private final int value;
    private final String description;

    DataScopeEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
