package com.agileboot.common.core.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.agileboot.common.core.page.PageDomain;
import com.agileboot.common.core.page.TableSupport;
import com.agileboot.common.utils.ServletHolderUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * web层通用数据处理
 *
 * @author ruoyi
 */
@Slf4j
public class BaseController {

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";


    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtil.parseDate(text));
            }
        });
    }



    @SuppressWarnings("rawtypes")
    protected Page getPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        return new Page(pageNum, pageSize);
    }



    public void fillOrderBy(QueryWrapper queryWrapper) {

        HttpServletRequest request = ServletHolderUtil.getRequest();
        Map<String, String> paramMap = ServletUtil.getParamMap(request);

        String orderColumn = paramMap.get(ORDER_BY_COLUMN);
        String orderDirection = paramMap.get(IS_ASC);

        queryWrapper.orderBy(StrUtil.isNotBlank(orderColumn), getSortDirection(orderDirection),
            StrUtil.toUnderlineCase(orderColumn));
    }


    public boolean getSortDirection(String isAsc) {
        boolean orderDirection = true;
        if (StrUtil.isNotEmpty(isAsc)) {
            // 兼容前端排序类型
            if ("ascending".equals(isAsc)) {
                orderDirection = true;
            } else if ("descending".equals(isAsc)) {
                orderDirection = false;
            }
        }
        return orderDirection;
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }


}
