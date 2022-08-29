package com.agileboot.admin.controller.system;


import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.common.utils.poi.CustomExcelUtil;
import com.agileboot.domain.common.BulkDeleteCommand;
import com.agileboot.domain.system.loginInfo.LoginInfoDTO;
import com.agileboot.domain.system.loginInfo.LoginInfoDomainService;
import com.agileboot.domain.system.loginInfo.LoginInfoQuery;
import com.agileboot.infrastructure.annotations.AccessLog;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/loginInfo")
@Validated
public class SysLoginInfoController extends BaseController {

    @Autowired
    private LoginInfoDomainService loginInfoDomainService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(LoginInfoQuery query) {
        PageDTO pageDTO = loginInfoDomainService.getLoginInfoList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, LoginInfoQuery query) {
        PageDTO pageDTO = loginInfoDomainService.getLoginInfoList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), LoginInfoDTO.class, response);
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @AccessLog(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public ResponseDTO remove(@PathVariable @NotNull @NotEmpty List<Long> infoIds) {
        loginInfoDomainService.deleteLoginInfo(new BulkDeleteCommand<>(infoIds));
        return ResponseDTO.ok();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @AccessLog(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ResponseDTO clean() {
        return ResponseDTO.fail(BusinessErrorCode.UNSUPPORTED_OPERATION);
    }
}
