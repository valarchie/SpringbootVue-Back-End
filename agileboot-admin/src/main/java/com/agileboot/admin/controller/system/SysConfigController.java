package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysConfig;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.exception.errors.BusinessErrorCode;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.cache.map.MapCache;
import com.agileboot.orm.entity.SysConfigXEntity;
import com.agileboot.orm.enums.cache.DictionaryData;
import com.agileboot.orm.service.ISysConfigXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
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
    public ResponseDTO<TableDataInfo> list(SysConfig config) {
        Page<SysConfigXEntity> page = getPage();
        QueryWrapper<SysConfigXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.like(StrUtil.isNotEmpty(config.getConfigName()), "config_name", config.getConfigName());
        configService.page(page, sysNoticeWrapper);
        return ResponseDTO.ok(getDataTable(page));
    }

    /**
     * 根据字典类型查询字典数据信息
     * 换成用Enum
     */
    @GetMapping(value = "/dict/{dictType}")
    public ResponseDTO<List> dictType(@PathVariable String dictType) {
        List<DictionaryData> dictionaryData = MapCache.dictionaryCache().get(dictType);
        return ResponseDTO.ok(dictionaryData);
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
        SysConfigXEntity byId = configService.getById(configId);
        return ResponseDTO.ok(byId);
    }


    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @AccessLog(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysConfig config) {
        // 键名 压根就不能修改
        SysConfigXEntity configEntity = configService.getById(config.getConfigId());

        if (configEntity == null) {
            return ResponseDTO.fail(BusinessErrorCode.OBJECT_NOT_FOUND);
        }
        configEntity.setConfigName(config.getConfigName());
        configEntity.setConfigValue(config.getConfigValue());
        configEntity.updateById();
        return ResponseDTO.ok();
    }



    /**
     *
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @AccessLog(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public ResponseDTO refreshCache() {
        // TODO 到时候看如何实现
        return ResponseDTO.ok();
    }
}
