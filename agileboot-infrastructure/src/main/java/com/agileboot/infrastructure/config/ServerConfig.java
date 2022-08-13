package com.agileboot.infrastructure.config;

import com.agileboot.common.utils.ServletHolderUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 服务相关配置
 *
 * @author ruoyi
 */
@Component
public class ServerConfig {

    /**
     * 获取完整的请求路径，包括：域名，端口，上下文访问路径
     * TODO 压根不用做成config
     * @return 服务地址
     */
    public String getUrl() {
        HttpServletRequest request = ServletHolderUtil.getRequest();
        return getDomain(request);
    }

    // TODO 需要改造一下
    public static String getDomain(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String contextPath = request.getServletContext().getContextPath();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
    }
}
