package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysOperLog;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.time.DatePicker;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.orm.entity.SysOperationLogXEntity;
import com.agileboot.orm.service.ISysOperationLogXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    @Autowired
    private ISysOperationLogXService operationLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public ResponseDTO<TableDataInfo> list(SysOperLog operLog) {

        Page<SysOperationLogXEntity> page = getPage();
        QueryWrapper<SysOperationLogXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(operLog.getBusinessType()!=null, "business_type", operLog.getBusinessType())
            .eq(operLog.getStatus() != null, "status", operLog.getStatus())
            .like(StrUtil.isNotEmpty(operLog.getOperName()), "user_name", operLog.getOperName())
            .like(StrUtil.isNotEmpty(operLog.getTitle()), "request_module", operLog.getTitle())
            .ge(operLog.getParams().get("beginTime") != null, "operation_time",
                DatePicker.getBeginOfTheDay(operLog.getParams().get("beginTime")))
            .le(operLog.getParams().get("endTime") != null, "operation_time",
                DatePicker.getEndOfTheDay(operLog.getParams().get("endTime")));
        fillOrderBy(queryWrapper);

        operationLogService.page(page, queryWrapper);
        List<SysOperLog> excelModels = page.getRecords().stream().map(SysOperLog::new)
            .collect(Collectors.toList());

        return ResponseDTO.ok(getDataTable(excelModels, page.getTotal()));
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog) {

        QueryWrapper<SysOperationLogXEntity> queryWrapper = new QueryWrapper<>();

        fillOrderBy(queryWrapper);

        List<SysOperationLogXEntity> list = operationLogService.list(queryWrapper);

        List<SysOperLog> excelModels = list.stream().map(SysOperLog::new).collect(Collectors.toList());
//        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
//        util.exportExcel(response, excelModels, "操作日志");
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public ResponseDTO remove(@PathVariable Long[] operIds) {
        QueryWrapper<SysOperationLogXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("operation_id", operIds);
        operationLogService.remove(queryWrapper);
        return ResponseDTO.ok();
    }

    @AccessLog(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public ResponseDTO clean() {
        return ResponseDTO.fail();
    }
}
