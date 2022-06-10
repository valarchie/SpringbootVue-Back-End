package com.ruoyi.web.controller.system;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.ResponseDTO;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.test.sys.po.SysConfigXEntity;
import com.ruoyi.system.domain.test.sys.service.ISysConfigXService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 参数配置 信息操作处理
 * TODO 改成系统配置应该更合理吧
 * TODO 需配置缓存 直接使用切面缓存    配置根本没有必要做成可配置的  多此一举
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {

    @Autowired
    private ISysConfigXService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config) {
        Page<SysConfigXEntity> page = getPage();
        QueryWrapper<SysConfigXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.like(StrUtil.isNotEmpty(config.getConfigName()), "config_name", config.getConfigName());
        configService.page(page, sysNoticeWrapper);
        return getDataTable(page);
    }

    /**
     * 完全没必要做导出
     * @param response
     * @param config
     */
//    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
//    @PreAuthorize("@ss.hasPermi('system:config:export')")
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SysConfig config) {
//        List<SysConfig> list = configService.selectConfigList(config);
//        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
//        util.exportExcel(response, list, "参数数据");
//    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public ResponseDTO getInfo(@PathVariable Long configId) {
        return ResponseDTO.success(configService.getById(configId));
    }

    /**
     * 新增参数配置 TODO 根本不用需要添加功能
     */
//    @PreAuthorize("@ss.hasPermi('system:config:add')")
//    @Log(title = "参数管理", businessType = BusinessType.INSERT)
//    @PostMapping
//    public ResponseDTO add(@Validated @RequestBody SysConfig config) {
//        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
//            return ResponseDTO.error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
//        }
//        config.setCreateBy(getUsername());
//        return toAjax(configService.insertConfig(config));
//    }
    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysConfig config) {
        // 键名 压根就不能修改
        SysConfigXEntity configEntity = configService.getById(config.getConfigId());

        if (configEntity == null) {
            return ResponseDTO.error("config does not exist.");
        }
        configEntity.setConfigName(config.getConfigName());
        configEntity.setConfigValue(config.getConfigValue());
        return toAjax(configEntity.updateById());
    }



    /**
     *
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public ResponseDTO refreshCache() {
        // TODO 到时候看如何实现
        return ResponseDTO.success();
    }
}
