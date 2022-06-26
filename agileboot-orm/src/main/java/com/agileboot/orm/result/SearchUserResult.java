package com.agileboot.orm.result;

import com.agileboot.orm.entity.SysUserXEntity;
import lombok.Data;

/**
 * 如果Entity的字段和复杂查询不匹配时   自定义类来接收
 */
@Data
public class SearchUserResult extends SysUserXEntity {

    private String deptName;
    private String deptLeader;

}
