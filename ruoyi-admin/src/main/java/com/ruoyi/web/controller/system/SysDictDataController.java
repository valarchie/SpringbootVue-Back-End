package com.ruoyi.web.controller.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.ResponseDTO;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.test.sys.po.SysDictDataXEntity;
import com.ruoyi.system.domain.test.sys.service.ISysDictDataXService;
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
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Autowired
    private ISysDictDataXService dictDataService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData) {
        Page<SysDictDataXEntity> page = getPage();
        QueryWrapper<SysDictDataXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotEmpty(dictData.getDictType()), "dict_type", dictData.getDictType())
            .eq(dictData.getDictValue() != null, "dict_value", dictData.getDictValue())
            .like(StrUtil.isNotEmpty(dictData.getDictLabel()), "dict_label", dictData.getDictLabel());
        dictDataService.page(page, queryWrapper);
        return getDataTable(page);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData) {
        Page<SysDictDataXEntity> page = getPage();
        QueryWrapper<SysDictDataXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotEmpty(dictData.getDictType()), "dict_type", dictData.getDictType())
            .eq(dictData.getDictValue() != null, "dict_value", dictData.getDictValue())
            .like(StrUtil.isNotEmpty(dictData.getDictLabel()), "dict_label", dictData.getDictLabel());
        fillOrderBy(queryWrapper);

        dictDataService.page(page, queryWrapper);
        List<SysDictData> excelModels = page.getRecords().stream().map(this::toSysDictData)
            .collect(Collectors.toList());

        ExcelUtil<SysDictData> util = new ExcelUtil<>(SysDictData.class);
        util.exportExcel(response, excelModels, "字典类型数据");
    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public ResponseDTO getInfo(@PathVariable Long dictCode) {
        // TODO 需要判空
        SysDictData sysDictData = toSysDictData(dictDataService.getById(dictCode));
        return ResponseDTO.success(sysDictData);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public ResponseDTO dictType(@PathVariable String dictType) {
        QueryWrapper<SysDictDataXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", dictType);
        List<SysDictDataXEntity> list = dictDataService.list(queryWrapper);

        List<SysDictData> excelModels = list.stream().map(this::toSysDictData)
            .collect(Collectors.toList());

        return ResponseDTO.success(excelModels);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysDictData dict) {

        SysDictDataXEntity entity = new SysDictDataXEntity();
        entity.setDictSort(Convert.toInt(dict.getDictSort()));
        entity.setDictLabel(dict.getDictLabel());
        entity.setDictValue(dict.getDictValue());
        entity.setDictType(dict.getDictType());
        entity.setCssClass(dict.getCssClass());
        entity.setListClass(dict.getListClass());
        entity.setIsDefault(dict.isDefault());
        entity.setCreatorId(getUserId());
        entity.setCreateName(getUsername());
        entity.setCreateTime(DateUtil.date());
        entity.setRemark(dict.getRemark());
        return toAjax(entity.insert());
    }

    /**
     * 修改保存字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysDictData dict) {

        SysDictDataXEntity entity = new SysDictDataXEntity();
        entity.setDictSort(Convert.toInt(dict.getDictSort()));
        entity.setDictLabel(dict.getDictLabel());
        entity.setDictValue(dict.getDictValue());
        entity.setDictType(dict.getDictType());
        entity.setCssClass(dict.getCssClass());
        entity.setListClass(dict.getListClass());
        entity.setIsDefault(dict.isDefault());
        entity.setCreatorId(getUserId());
        entity.setCreateName(getUsername());
        entity.setCreateTime(DateUtil.date());
        entity.setRemark(dict.getRemark());

        entity.setDictDataId(dict.getDictCode());
        return toAjax(entity.updateById());
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public ResponseDTO remove(@PathVariable Long[] dictDataIds) {
        QueryWrapper<SysDictDataXEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("dict_date_id", dictDataIds);
        return toAjax(dictDataService.remove(queryWrapper));
    }

    public SysDictData toSysDictData(SysDictDataXEntity entity) {
        SysDictData model = new SysDictData();
        model.setDictCode(entity.getDictDataId());
        model.setDictSort(Long.valueOf(entity.getDictSort()));
        model.setDictLabel(entity.getDictLabel());
        model.setDictValue(entity.getDictValue());
        model.setDictType(entity.getDictType());
        model.setCssClass(entity.getCssClass());
        model.setListClass(entity.getListClass());
        model.setIsDefault(Convert.toStr(entity.getIsDefault()));
        model.setStatus(Convert.toStr(entity.getStatus()));
        return model;
    }
}
