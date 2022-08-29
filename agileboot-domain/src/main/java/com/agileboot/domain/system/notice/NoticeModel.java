package com.agileboot.domain.system.notice;

import com.agileboot.orm.entity.SysNoticeXEntity;
import com.agileboot.orm.enums.dictionary.CommonStatusEnum;
import com.agileboot.orm.enums.dictionary.NoticeTypeEnum;
import com.agileboot.orm.enums.interfaces.BasicEnumUtil;
import lombok.Data;

@Data
public class NoticeModel extends SysNoticeXEntity {


    public void checkFields() {

        Integer noticeType = this.getNoticeType();
        BasicEnumUtil.fromValue(NoticeTypeEnum.class, noticeType);

        Integer status = this.getStatus();
        BasicEnumUtil.fromValue(CommonStatusEnum.class, status);

    }

}
