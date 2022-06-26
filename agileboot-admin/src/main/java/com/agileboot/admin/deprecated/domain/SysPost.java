package com.agileboot.admin.deprecated.domain;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.annotation.Excel;
import com.agileboot.common.annotation.Excel.ColumnType;
import com.agileboot.common.core.domain.BaseEntity;
import com.agileboot.orm.entity.SysPostXEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 岗位表 sys_post
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Deprecated
public class SysPost extends BaseEntity {


    public SysPost(SysPostXEntity entity) {

        this.postId = entity.getPostId();
        this.postCode = entity.getPostCode();
        this.postName = entity.getPostName();
        this.postSort = entity.getPostSort() + "";
        this.status = entity.getStatus() + "";

    }

    public SysPostXEntity toEntity() {
        SysPostXEntity entity = new SysPostXEntity();
        entity.setPostId(this.postId);
        entity.setPostCode(this.postCode);
        entity.setPostName(this.postName);
        entity.setStatus(Convert.toInt(this.status));
        entity.setPostSort(Convert.toInt(this.postSort));

        return entity;
    }



    private static final long serialVersionUID = 1L;

    /**
     * 岗位序号
     */
    @Excel(name = "岗位序号", cellType = ColumnType.NUMERIC)
    private Long postId;

    /**
     * 岗位编码
     */
    @Excel(name = "岗位编码")
    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 0, max = 64, message = "岗位编码长度不能超过64个字符")
    private String postCode;

    /**
     * 岗位名称
     */
    @Excel(name = "岗位名称")
    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 0, max = 50, message = "岗位名称长度不能超过50个字符")
    private String postName;

    /**
     * 岗位排序
     */
    @Excel(name = "岗位排序")
    @NotBlank(message = "显示顺序不能为空")
    private String postSort;

    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 用户是否存在此岗位标识 默认不存在
     */
    private boolean flag = false;

}
