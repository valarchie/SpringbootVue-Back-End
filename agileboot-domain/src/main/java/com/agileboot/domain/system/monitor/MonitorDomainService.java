package com.agileboot.domain.system.monitor;

import cn.hutool.core.util.StrUtil;
import com.agileboot.domain.system.monitor.dto.RedisCacheInfoDTO;
import com.agileboot.domain.system.monitor.dto.RedisCacheInfoDTO.CommonStatusDTO;
import com.agileboot.infrastructure.cache.RedisUtil;
import com.agileboot.infrastructure.cache.redis.CacheKeyEnum;
import com.agileboot.infrastructure.web.domain.SysUserOnline;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.domain.server.ServerInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MonitorDomainService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;


    public RedisCacheInfoDTO getRedisCacheInfo() {

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

        return cacheInfo;
    }

    public List<SysUserOnline> getOnlineUserList(String userName, String ipaddr) {

        Collection<String> keys = redisUtil.keys(CacheKeyEnum.LOGIN_USER_KEY.key() + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys) {
            LoginUser user = redisUtil.getCacheObject(key);
            SysUserOnline sysUserOnline = loginUserToUserOnline(user);

            if (StrUtil.isAllNotEmpty(ipaddr, userName) &&
                StrUtil.equals(ipaddr, user.getIpaddr()) && StrUtil.equals(userName, user.getUsername())) {
                userOnlineList.add(sysUserOnline);
            }
            if (StrUtil.isNotEmpty(ipaddr) && StrUtil.equals(ipaddr, user.getIpaddr())) {
                userOnlineList.add(sysUserOnline);
            }
            if (StrUtil.isNotEmpty(userName) && StrUtil.equals(userName, user.getUsername())) {
                userOnlineList.add(sysUserOnline);
            }

            userOnlineList.add(sysUserOnline);
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));

        return userOnlineList;
    }

    public ServerInfo getServerInfo() {
        return ServerInfo.fillInfo();
    }


    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(LoginUser user) {
        if (user == null) {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        // TODO 利用缓存类   获取deptName
        sysUserOnline.setDeptName("depart 1");
        return sysUserOnline;
    }
}
