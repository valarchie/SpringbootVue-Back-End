package com.agileboot.common.core.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.Data;

/**
 * @author valarchie
 */
@Data
public class PageDTO {
    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<?> rows;

    public PageDTO(List<?> list) {
        this.rows = list;
        this.total = list.size();
    }

    public PageDTO(Page page) {
        this.rows = page.getRecords();
        this.total = page.getTotal();
    }

    public PageDTO(List<?> list, Long count) {
        this.rows = list;
        this.total = count;
    }

}
