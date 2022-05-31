package com.ruoyi.generator.domain;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成业务字段表 gen_table_column
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GenTableColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private Long columnId;

    /**
     * 归属表编号
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列描述
     */
    private String columnComment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * JAVA类型
     */
    private String javaType;

    /**
     * JAVA字段名
     */
    @NotBlank(message = "Java属性不能为空")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    private String isPk;

    /**
     * 是否自增（1是）
     */
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    private String isQuery;

    /**
     * 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）
     */
    private String queryType;

    /**
     * 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sort;

    public boolean isPk() {
        return isPk(this.isPk);
    }

    public boolean isPk(String isPk) {
        return isPk != null && StrUtil.equals("1", isPk);
    }

    public boolean isIncrement() {
        return isIncrement(this.isIncrement);
    }

    public boolean isIncrement(String isIncrement) {
        return isIncrement != null && StrUtil.equals("1", isIncrement);
    }

    public boolean isRequired() {
        return isRequired(this.isRequired);
    }

    public boolean isRequired(String isRequired) {
        return isRequired != null && StrUtil.equals("1", isRequired);
    }


    public boolean isInsert() {
        return isInsert(this.isInsert);
    }

    public boolean isInsert(String isInsert) {
        return isInsert != null && StrUtil.equals("1", isInsert);
    }

    public boolean isEdit() {
        return isEdit(this.isEdit);
    }

    public boolean isEdit(String isEdit) {
        return isEdit != null && StrUtil.equals("1", isEdit);
    }


    public boolean isList() {
        return isList(this.isList);
    }

    public boolean isList(String isList) {
        return isList != null && StrUtil.equals("1", isList);
    }

    public boolean isQuery() {
        return isQuery(this.isQuery);
    }

    public boolean isQuery(String isQuery) {
        return isQuery != null && StrUtil.equals("1", isQuery);
    }


    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField) {
        return StrUtil.equalsAnyIgnoreCase(javaField,
            // BaseEntity
            "createBy", "createTime", "updateBy", "updateTime", "remark",
            // TreeEntity
            "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isUsableColumn() {
        return isUsableColumn(javaField);
    }

    public static boolean isUsableColumn(String javaField) {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StrUtil.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
    }

    public String readConverterExp() {
        String remarks = StrUtil.subBetween(this.columnComment, "（", "）");
        StringBuffer sb = new StringBuffer();
        if (StrUtil.isNotEmpty(remarks)) {
            for (String value : remarks.split(" ")) {
                if (StrUtil.isNotEmpty(value)) {
                    Object startStr = value.subSequence(0, 1);
                    String endStr = value.substring(1);
                    sb.append("").append(startStr).append("=").append(endStr).append(",");
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return this.columnComment;
        }
    }
}
