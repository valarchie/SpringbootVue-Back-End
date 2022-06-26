package com.agileboot.orm.enums;

/**
 * @author valarchie
 */
public enum UserStatusEnum {

    /**
     * 用户账户状态
     */
    NORMAL(0, "正常"),
    DISABLED(1, "禁用"),
    FROZEN(2, "冻结");

    private final int value;
    private final String description;

    UserStatusEnum(int value, String description) {
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
