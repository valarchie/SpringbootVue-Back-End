package com.agileboot.admin.controller.monitor;


import com.agileboot.admin.deprecated.domain.SysLogininfor;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.poi.ExcelUtil;
import com.agileboot.orm.entity.SysLoginInfoXEntity;
import com.agileboot.orm.query.system.LoginInfoQuery;
import com.agileboot.orm.service.ISysLoginInfoXService;
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
 * 系统访问记录
 *
 * @author ruoyi
 */
@RestController
// TODO 记得改这个logininfor的命名
@RequestMapping("/monitor/logininfor")
public class SysLoginInfoController extends BaseController {

    @Autowired
    private ISysLoginInfoXService loginInfoService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public ResponseDTO<TableDataInfo> list(LoginInfoQuery loginInfoQuery) {

        Page<SysLoginInfoXEntity> page = getPage();
        QueryWrapper queryWrapper = loginInfoQuery.generateQueryWrapper();

        loginInfoService.page(page, queryWrapper);
        return ResponseDTO.ok(getDataTable(page));
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, LoginInfoQuery loginInfoQuery) {

        Page<SysLoginInfoXEntity> page = getPage();

        QueryWrapper queryWrapper = loginInfoQuery.generateQueryWrapper();

        loginInfoService.page(page, queryWrapper);
        List<SysLogininfor> excelModels = page.getRecords().stream().map(SysLogininfor::new)
            .collect(Collectors.toList());

        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, excelModels, "登录日志");
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public ResponseDTO remove(@PathVariable List<Long> infoIds) {
        QueryWrapper<SysLoginInfoXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("info_id", infoIds);
        return ResponseDTO.ok();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ResponseDTO clean() {
        return ResponseDTO.fail();
    }
}
