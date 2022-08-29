package com.agileboot.domain.common;

import cn.hutool.core.collection.CollUtil;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.BusinessErrorCode;
import java.util.List;
import lombok.Data;

@Data
public class BulkDeleteCommand<T> {

    public BulkDeleteCommand(List<T> idList) {
        if (CollUtil.isEmpty(idList)) {
            throw new ApiException(BusinessErrorCode.BULK_DELETE_IDS_IS_INVALID);
        }

        this.ids = idList;
    }

    private List<T> ids;

}
