package com.agileboot.common.utils.file;

/**
 * 媒体类型工具类
 *
 * @author ruoyi TODO 待改进
 */
public class FileTypesConstant {

    public static final String[] IMAGE_EXTENSIONS = {"bmp", "gif", "jpg", "jpeg", "png"};

    public static final String[] FLASH_EXTENSIONS = {"swf", "flv"};

    public static final String[] MEDIA_EXTENSIONS = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg",
        "asf", "rm", "rmvb"};

    public static final String[] VIDEO_EXTENSIONS = {"mp4", "avi", "rmvb"};

    public static final String[] ALLOWED_DOWNLOAD_EXTENSIONS = {
        // 图片
        "bmp", "gif", "jpg", "jpeg", "png",
        // word excel powerpoint
        "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
        // 压缩文件
        "rar", "zip", "gz", "bz2",
        // 视频格式
        "mp4", "avi", "rmvb",
        // pdf
        "pdf"};

}
