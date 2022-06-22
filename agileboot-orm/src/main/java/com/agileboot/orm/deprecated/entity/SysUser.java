package com.agileboot.orm.deprecated.entity;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.annotation.Excel;
import com.agileboot.common.annotation.Excel.Type;
import com.agileboot.common.annotation.Excels;
import com.agileboot.common.core.domain.BaseEntity;
import com.agileboot.orm.po.SysUserXEntity;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public SysUserXEntity toEntity() {
        SysUserXEntity entity = new SysUserXEntity();

        entity.setUserId(this.userId);
        entity.setDeptId(this.deptId);
        entity.setUserName(this.userName);
        entity.setNickName(this.nickName);
        entity.setEmail(this.email);
        entity.setPhoneNumber(this.phonenumber);
        entity.setSex(Convert.toInt(this.sex));
        entity.setAvatar(this.avatar);
        entity.setPassword(this.password);
        // TODO 密码没加盐？
        entity.setStatus(Convert.toInt(this.status));
        return entity;
    }

    public SysUser(SysUserXEntity entity) {

        this.userId = entity.getUserId();
        this.deptId = entity.getDeptId();
        this.userName = entity.getUserName();
        this.nickName = entity.getNickName();
        this.email = entity.getEmail();
        this.phonenumber = entity.getPhoneNumber();
        this.sex = entity.getSex() + "";
        this.avatar = entity.getAvatar();
        this.password = entity.getPassword();
        this.status = entity.getStatus()+"";
    }


    /**
     * 用户ID
     */
//    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    /**
     * 部门ID
     */
//    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 用户账号
     */
    @Excel(name = "登录名称")
//    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
//    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐加密
     */
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /**
     * 部门对象
     */
    @Excels({
        @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
        @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 角色ID
     */
    private Long roleId;

    public boolean isAdmin() {
        return userId != null && 1L == userId;
    }
    // TODO 用ID来判断 实在不靠谱
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

}
