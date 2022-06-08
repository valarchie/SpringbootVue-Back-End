package com.ruoyi.web.controller.monitor;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.ResponseDTO;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.time.DatePicker;
import com.ruoyi.system.domain.SysLogininfor;
import com.ruoyi.system.domain.test.sys.po.SysLoginInfoXEntity;
import com.ruoyi.system.domain.test.sys.service.ISysLoginInfoXService;
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
public class SysLogininforController extends BaseController {

    @Autowired
    private ISysLoginInfoXService loginInfoService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor) {

        Page<SysLoginInfoXEntity> page = getPage();
        QueryWrapper<SysLoginInfoXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StrUtil.isNotEmpty(logininfor.getIpaddr()), "ip_address", logininfor.getIpaddr())
            .eq(StrUtil.isNotEmpty(logininfor.getStatus()), "status", logininfor.getStatus())
            .like(StrUtil.isNotEmpty(logininfor.getUserName()), "user_name", logininfor.getUserName())
            .ge(logininfor.getParams().get("beginTime") != null, "login_time",
                DatePicker.getBeginOfTheDay(logininfor.getParams().get("beginTime")))
            .le(logininfor.getParams().get("endTime") != null, "login_time",
                DatePicker.getEndOfTheDay(logininfor.getParams().get("endTime")));
        fillOrderBy(queryWrapper);

        loginInfoService.page(page, queryWrapper);
        return getDataTable(page);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor) {

        Page<SysLoginInfoXEntity> page = getPage();
        QueryWrapper<SysLoginInfoXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StrUtil.isNotEmpty(logininfor.getIpaddr()), "ip_address", logininfor.getIpaddr())
            .eq(StrUtil.isNotEmpty(logininfor.getStatus()), "status", logininfor.getStatus())
            .like(StrUtil.isNotEmpty(logininfor.getUserName()), "user_name", logininfor.getUserName())
            .ge(logininfor.getParams().get("beginTime") != null, "login_time",
                DatePicker.getBeginOfTheDay(logininfor.getParams().get("beginTime")))
            .le(logininfor.getParams().get("endTime") != null, "login_time",
                DatePicker.getEndOfTheDay(logininfor.getParams().get("endTime")));

        fillOrderBy(queryWrapper);

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
        return toAjax(loginInfoService.remove(queryWrapper));
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ResponseDTO clean() {
        return ResponseDTO.error("不支持全表清空");
    }
}
