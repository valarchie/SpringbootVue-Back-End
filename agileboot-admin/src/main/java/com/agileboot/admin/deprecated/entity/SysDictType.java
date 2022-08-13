package com.agileboot.admin.deprecated.entity;

import com.agileboot.common.annotation.ExcelColumn;
import com.agileboot.common.annotation.ExcelColumn.ColumnType;
import com.agileboot.common.core.dto.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型表 sys_dict_type
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class SysDictType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @ExcelColumn(name = "字典主键", cellType = ColumnType.NUMERIC)
    private Long dictId;

    /**
     * 字典名称
     */
    @ExcelColumn(name = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    @Size(min = 0, max = 100, message = "字典类型名称长度不能超过100个字符")
    private String dictName;

    /**
     * 字典类型
     */
    @ExcelColumn(name = "字典类型")
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型类型长度不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelColumn(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}

