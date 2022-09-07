package com.agileboot.common.exception.errors;

/**
 * 40000~49999 为业务逻辑 错误码 （无异常，代码正常流转，并返回提示给用户）
 * @author valarchie
 */
public enum BusinessErrorCode implements ErrorCodeInterface{

    /**
     * 业务错误码
     */
    OBJECT_NOT_FOUND(Module.COMMON, 1, "找不到ID为%s 的%s"),

    UNSUPPORTED_OPERATION(Module.COMMON, 2, "不支持的操作"),

    BULK_DELETE_IDS_IS_INVALID(Module.COMMON, 3, "批量参数ID列表为空"),

    FORBIDDEN_TO_MODIFY_ADMIN(Module.DATA_SECURITY, 1, "不允许修改管理员的信息"),

    LOGIN_WRONG_USER_PASSWORD(Module.LOGIN, 1, "用户密码错误，请重输"),

    LOGIN_ERROR(Module.LOGIN, 2, "登录失败：%s"),

    CAPTCHA_CODE_WRONG(Module.LOGIN, 3, "验证码错误"),

    CAPTCHA_CODE_EXPIRE(Module.LOGIN, 4, "验证码过期"),

    CAPTCHA_CODE_NULL(Module.LOGIN, 5, "验证码为空"),

    UPLOAD_FILE_TYPE_NOT_ALLOWED(Module.UPLOAD, 1, "不允许上传的文件类型，仅允许：%s"),

    UPLOAD_FILE_NAME_EXCEED_MAX_LENGTH(Module.UPLOAD, 2, "文件名长度超过：%s "),

    UPLOAD_FILE_SIZE_EXCEED_MAX_SIZE(Module.UPLOAD, 3, "文件名大小超过：%s MB"),

    UPLOAD_IMPORT_EXCEL_FAILED(Module.UPLOAD, 4, "导入excel失败：%s"),

    UPLOAD_FILE_IS_EMPTY(Module.UPLOAD, 5, "上传文件为空"),

    USER_NON_EXIST(Module.USER, 1, "登录用户：%s 不存在"),

    USER_IS_DISABLE(Module.USER, 2, "对不起， 您张的账号：%s 已停用"),

    USER_CACHE_IS_EXPIRE(Module.USER, 3, "用户缓存信息已经过期"),

    USER_FAIL_TO_GET_USER_ID(Module.USER, 3, "获取用户ID失败"),

    USER_FAIL_TO_GET_DEPT_ID(Module.USER, 4, "获取用户部门ID失败"),

    USER_FAIL_TO_GET_ACCOUNT(Module.USER, 5, "获取用户账户失败"),

    USER_FAIL_TO_GET_USER_INFO(Module.USER, 6, "获取用户信息失败"),

    USER_IMPORT_DATA_IS_NULL(Module.USER, 7, "导入的用户为空"),

    CONFIG_VALUE_IS_NOT_ALLOW_TO_EMPTY(Module.CONFIG, 1, "参数键值不允许为空"),

    CONFIG_VALUE_IS_NOT_IN_OPTIONS(Module.CONFIG, 2, "参数键值不存在列表中"),

    POST_NAME_IS_NOT_UNIQUE(Module.POST, 1, "岗位名称:%s, 已存在"),

    POST_CODE_IS_NOT_UNIQUE(Module.DEPT, 2, "岗位编号:%s, 已存在"),

    DEPT_NAME_IS_NOT_UNIQUE(Module.DEPT, 3, "部门名称:%s, 已存在"),

    DEPT_PARENT_ID_IS_NOT_ALLOWED_SELF(Module.DEPT, 4, "父级部门不能选择自己"),

    DEPT_STATUS_ID_IS_NOT_ALLOWED_CHANGE(Module.DEPT, 5, "子部门还有正在启用的部门，暂时不能停用该部门"),

    DEPT_EXIST_CHILD_DEPT_NOT_ALLOW_DELETE(Module.DEPT, 6, "该部门存在下级部门不允许删除"),

    DEPT_EXIST_LINK_USER_NOT_ALLOW_DELETE(Module.DEPT, 6, "该部门存在关联的用户不允许删除"),

    DEPT_PARENT_DEPT_NO_EXIST_OR_DISABLED(Module.DEPT, 7, "该父级部门不存在或已停用"),

    MENU_NAME_IS_NOT_UNIQUE(Module.SYSTEM, 1, "新增菜单:%s 失败，菜单名称已存在"),

    MENU_EXTERNAL_LINK_MUST_BE_HTTP(Module.SYSTEM, 2, "菜单外链必须以 http(s)://开头"),

    MENU_PARENT_ID_NOT_ALLOW_SELF(Module.SYSTEM, 3, "父级菜单不能选择自身"),

    MENU_EXIST_CHILD_MENU_NOT_ALLOW_DELETE(Module.SYSTEM, 4, "存在子菜单不允许删除"),

    MENU_ALREADY_ASSIGN_TO_ROLE_NOT_ALLOW_DELETE(Module.SYSTEM, 4, "菜单已分配给角色，不允许"),


    ;

    enum Module {
        /**
         * 普通模块
         */
        COMMON(0),

        DATA_SECURITY(1),

        LOGIN(2),

        UPLOAD(3),

        USER(4),

        CONFIG(5),

        POST(6),

        DEPT(7),

        SYSTEM(8),

        ;

        private final int code;

        Module(int code) { this.code = code; }

        int code() { return code * 100; }
    }

    private final int code;
    private final String msg;

    private static final int BASE_CODE = 40000;

    BusinessErrorCode(Module module, int code, String msg) {
        this.code = BASE_CODE + module.code() + code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.msg;
    }

    @Override
    public String i18n() {
        return this.code + "_" + this.name();
    }

}
