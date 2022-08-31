package com.agileboot.admin.controller.system;

import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import com.agileboot.common.utils.poi.CustomExcelUtil;
import com.agileboot.domain.common.BulkDeleteCommand;
import com.agileboot.domain.system.operationLog.OperationLogDTO;
import com.agileboot.domain.system.operationLog.OperationLogDomainService;
import com.agileboot.domain.system.operationLog.OperationLogQuery;
import com.agileboot.infrastructure.annotations.AccessLog;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/operationLog")
public class SysOperationLogController extends BaseController {

    @Autowired
    private OperationLogDomainService operationLogDomainService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(OperationLogQuery query) {
        PageDTO pageDTO = operationLogDomainService.getOperationLogList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperationLogQuery query) {
        PageDTO pageDTO = operationLogDomainService.getOperationLogList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), OperationLogDTO.class, response);
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operationIds}")
    public ResponseDTO remove(@PathVariable List<Long> operationIds) {
        operationLogDomainService.deleteOperationLog(new BulkDeleteCommand<>(operationIds));
        return ResponseDTO.ok();
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public ResponseDTO clean() {
        return ResponseDTO.fail(BusinessErrorCode.UNSUPPORTED_OPERATION);
    }
}
