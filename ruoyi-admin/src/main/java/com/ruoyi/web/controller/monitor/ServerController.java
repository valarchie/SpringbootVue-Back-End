package com.ruoyi.web.controller.monitor;

import com.ruoyi.common.core.domain.ResponseDTO;
import com.ruoyi.framework.web.domain.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public ResponseDTO getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return ResponseDTO.success(server);
    }
}
