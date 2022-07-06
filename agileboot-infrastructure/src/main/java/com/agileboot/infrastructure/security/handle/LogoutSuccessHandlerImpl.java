package com.agileboot.infrastructure.security.handle;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.Rdto;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.common.utils.ServletHolderUtil;
import com.agileboot.infrastructure.manager.AsyncManager;
import com.agileboot.infrastructure.manager.factory.AsyncFactory;
import com.agileboot.infrastructure.web.service.TokenService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * 自定义退出处理类 返回成功
 *
 * @author ruoyi
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLoginInfo(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletHolderUtil.renderString(response, JSONUtil.toJsonStr(Rdto.error(HttpStatus.HTTP_OK, "退出成功")));
    }
}
