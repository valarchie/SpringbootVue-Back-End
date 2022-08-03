package com.agileboot.common.constant;


/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public class Constants {

    public static final int KB = 1024;

    public static final int MB = KB * 1024;

    public static final int GB = MB * 1024;

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";


    public static class TokenConstants {
        /**
         * 令牌
         */
        public static final String TOKEN = "token";

        /**
         * 令牌前缀
         */
        public static final String TOKEN_PREFIX = "Bearer ";

        /**
         * 令牌前缀
         */
        public static final String LOGIN_USER_KEY = "login_user_key";

    }








}
