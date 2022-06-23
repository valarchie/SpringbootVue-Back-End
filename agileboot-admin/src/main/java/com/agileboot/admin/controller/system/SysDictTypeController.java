package com.agileboot.admin.controller.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.poi.ExcelUtil;
import com.agileboot.orm.deprecated.entity.SysDictType;
import com.agileboot.orm.po.SysDictTypeXEntity;
import com.agileboot.orm.service.ISysDictTypeXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {

    @Autowired
    private ISysDictTypeXService dictTypeService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        Page<SysDictTypeXEntity> page = getPage();
        QueryWrapper<SysDictTypeXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.like(StrUtil.isNotEmpty(dictType.getDictName()), "dict_name", dictType.getDictName())
            .like(dictType.getDictType() != null, "dict_type", dictType.getDictType())
            .eq(dictType.getStatus() != null, "status", dictType.getStatus());
        dictTypeService.page(page, sysNoticeWrapper);
        return getDataTable(page);
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType) {
        Page<SysDictTypeXEntity> page = getPage();
        QueryWrapper<SysDictTypeXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotEmpty(dictType.getDictName()), "dict_name", dictType.getDictName())
            .like(dictType.getDictType() != null, "dict_type", dictType.getDictType())
            .eq(dictType.getStatus() != null, "status", dictType.getStatus());
        fillOrderBy(queryWrapper);

        dictTypeService.page(page, queryWrapper);
        List<SysDictType> excelModels = page.getRecords().stream().map(o->toSysDictType(o))
            .collect(Collectors.toList());

        ExcelUtil<SysDictType> util = new ExcelUtil<>(SysDictType.class);
        util.exportExcel(response, excelModels, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public ResponseDTO getInfo(@PathVariable Long dictId) {
        // TODO 需要判空
        return ResponseDTO.success(dictTypeService.getById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysDictType dict) {

        SysDictTypeXEntity entity = new SysDictTypeXEntity();
        entity.setDictType(dict.getDictType());
        entity.setDictName(dict.getDictName());
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getUsername());
        entity.setCreateTime(DateUtil.date());

        return toAjax(dictTypeService.save(entity));
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysDictType dict) {

        SysDictTypeXEntity entity = new SysDictTypeXEntity();
        entity.setDictId(dict.getDictId());
        entity.setDictType(dict.getDictType());
        entity.setDictName(dict.getDictName());
        entity.setCreatorId(getUserId());
        entity.setCreatorName(getUsername());
        entity.setCreateTime(DateUtil.date());

        return toAjax(dictTypeService.updateById(entity));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public ResponseDTO remove(@PathVariable Long[] dictIds) {
        QueryWrapper<SysDictTypeXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("info_id", dictIds);
        return toAjax(dictTypeService.remove(queryWrapper));
    }

    /**
     * 刷新字典缓存
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public ResponseDTO refreshCache() {
        // TODO 清缓存
//        dictTypeService.resetDictCache();
        return ResponseDTO.success();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public ResponseDTO optionSelect() {
        List<SysDictTypeXEntity> dictTypes = dictTypeService.list();
        List<SysDictType> models = dictTypes.stream().map(this::toSysDictType)
            .collect(Collectors.toList());
        return ResponseDTO.success(models);
    }



    public SysDictType toSysDictType(SysDictTypeXEntity entity) {
        SysDictType model = new SysDictType();
        model.setDictId(entity.getDictId());
        model.setDictName(entity.getDictName());
        model.setDictType(entity.getDictType());
        model.setStatus(Convert.toStr(entity.getStatus()));
        return model;
    }

}
