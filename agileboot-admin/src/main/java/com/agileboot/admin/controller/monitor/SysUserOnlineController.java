package com.agileboot.admin.controller.monitor;

import cn.hutool.core.util.StrUtil;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.redis.CacheKeyEnum;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.web.domain.SysUserOnline;
import com.agileboot.infrastructure.web.service.SysUserOnlineServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线用户监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {

    @Autowired
    private SysUserOnlineServiceImpl userOnlineService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisCacheService redisCacheService;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public ResponseDTO<TableDataInfo> list(String ipaddr, String userName) {
        Collection<String> keys = redisUtil.keys(CacheKeyEnum.LOGIN_USER_KEY.key() + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys) {
            LoginUser user = redisUtil.getCacheObject(key);
            if (StrUtil.isNotEmpty(ipaddr) && StrUtil.isNotEmpty(userName)) {
                if (StrUtil.equals(ipaddr, user.getIpaddr()) && StrUtil.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            } else if (StrUtil.isNotEmpty(ipaddr)) {
                if (StrUtil.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            } else if (StrUtil.isNotEmpty(userName)) {
                if (StrUtil.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return ResponseDTO.ok(getDataTable(userOnlineList));
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @AccessLog(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public ResponseDTO forceLogout(@PathVariable String tokenId) {
        redisCacheService.loginUserCache.delete(tokenId);
        return ResponseDTO.ok();
    }
}
