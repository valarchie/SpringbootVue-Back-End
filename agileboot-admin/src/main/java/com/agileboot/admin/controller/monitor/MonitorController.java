package com.agileboot.admin.controller.monitor;

import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.domain.system.monitor.MonitorDomainService;
import com.agileboot.domain.system.monitor.dto.RedisCacheInfoDTO;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.cache.redis.RedisCacheService;
import com.agileboot.infrastructure.web.domain.SysUserOnline;
import com.agileboot.infrastructure.web.domain.server.ServerInfo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MonitorDomainService monitorDomainService;

    @Autowired
    private RedisCacheService redisCacheService;

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/cacheInfo")
    public ResponseDTO<RedisCacheInfoDTO> getRedisCacheInfo() throws Exception {
        RedisCacheInfoDTO redisCacheInfo = monitorDomainService.getRedisCacheInfo();
        return ResponseDTO.ok(redisCacheInfo);
    }


    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping("/serverInfo")
    public ResponseDTO<ServerInfo> getServerInfo() {
        ServerInfo serverInfo = monitorDomainService.getServerInfo();
        return ResponseDTO.ok(serverInfo);
    }

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/onlineUser/list")
    public ResponseDTO<TableDataInfo> list(String ipaddr, String userName) {
        List<SysUserOnline> onlineUserList = monitorDomainService.getOnlineUserList(userName, ipaddr);
        return ResponseDTO.ok(getDataTable(onlineUserList));
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @AccessLog(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/onlineUser/{tokenId}")
    public ResponseDTO forceLogout(@PathVariable String tokenId) {
        redisCacheService.loginUserCache.delete(tokenId);
        return ResponseDTO.ok();
    }


}
