package com.agileboot.admin.controller.monitor;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.response.RedisCacheInfoDTO;
import com.agileboot.admin.response.RedisCacheInfoDTO.CommonStatusDTO;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.loginuser.LoginUser;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.redis.CacheKeyEnum;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.web.domain.ServerInfo;
import com.agileboot.infrastructure.web.domain.SysUserOnline;
import com.agileboot.infrastructure.web.service.SysUserOnlineServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysUserOnlineServiceImpl userOnlineService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisCacheService redisCacheService;

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/cache")
    public ResponseDTO<RedisCacheInfoDTO> getRedisCacheInfo() throws Exception {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute(
            (RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        if(commandStats == null) {
            throw new RuntimeException("找不到对应的redis信息。");
        }

        RedisCacheInfoDTO cacheInfo = new RedisCacheInfoDTO();

        cacheInfo.setInfo(info);
        cacheInfo.setDbSize(dbSize);
        cacheInfo.setCommandStats(new ArrayList<>());

        commandStats.stringPropertyNames().forEach(key -> {
            String property = commandStats.getProperty(key);

            RedisCacheInfoDTO.CommonStatusDTO commonStatus = new CommonStatusDTO();
            commonStatus.setName(StrUtil.removePrefix(key, "cmdstat_"));
            commonStatus.setValue(StrUtil.subBetween(property, "calls=", ",usec"));

            cacheInfo.getCommandStats().add(commonStatus);
        });

        return ResponseDTO.ok(cacheInfo);
    }


    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping("/server")
    public ResponseDTO<ServerInfo> getServerInfo() throws Exception {
        ServerInfo serverInfo = ServerInfo.fillInfo();
        return ResponseDTO.ok(serverInfo);
    }

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/online/list")
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
    @DeleteMapping("/online/{tokenId}")
    public ResponseDTO forceLogout(@PathVariable String tokenId) {
        redisCacheService.loginUserCache.delete(tokenId);
        return ResponseDTO.ok();
    }


}
