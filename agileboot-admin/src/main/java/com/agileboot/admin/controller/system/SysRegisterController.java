package com.agileboot.admin.controller.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.domain.model.RegisterBody;
import com.agileboot.infrastructure.web.service.SysRegisterService;
import com.springvue.orm.domain.test.sys.service.ISysConfigXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController {

    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigXService configService;

    @PostMapping("/register")
    public ResponseDTO register(@RequestBody RegisterBody user) {
        if (!(Convert.toBool(configService.getConfigValueByKey("sys.account.registerUser")))) {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StrUtil.isEmpty(msg) ? success() : error(msg);
    }
}
