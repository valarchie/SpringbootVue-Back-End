package com.ruoyi.common.core.page;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.servlet.ServletUtil;
import com.ruoyi.common.utils.ServletHolderUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * 表格数据处理
 *
 * @author ruoyi
 */
public class TableSupport {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {

        HttpServletRequest request = ServletHolderUtil.getRequest();
        Map<String, String> paramMap = ServletUtil.getParamMap(request);

        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(paramMap.get(PAGE_NUM), 1));
        pageDomain.setPageSize(Convert.toInt(paramMap.get(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(paramMap.get(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(paramMap.get(IS_ASC));
        pageDomain.setReasonable(Convert.toBool(paramMap.get(REASONABLE)));
        return pageDomain;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
