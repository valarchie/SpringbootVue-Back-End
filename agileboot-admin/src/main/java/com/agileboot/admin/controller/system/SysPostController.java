package com.agileboot.admin.controller.system;

import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysPost;
import com.agileboot.common.annotation.Log;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.domain.Rdto;
import com.agileboot.common.core.page.TableDataInfo;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.common.utils.poi.ExcelUtil;
import com.agileboot.orm.entity.SysPostXEntity;
import com.agileboot.orm.service.ISysPostXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 岗位信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController {

    @Autowired
    private ISysPostXService postService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post) {
        Page<SysPostXEntity> page = getPage();
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(post.getStatus() != null, "status", post.getStatus())
            .eq(post.getPostCode() != null, "post_code", post.getPostCode())
            .eq(post.getPostId() != null, "post_id", post.getPostId())
            .like(StrUtil.isNotEmpty(post.getPostName()), "post_name", post.getPostName());

        postService.page(page, queryWrapper);
        return getDataTable(page);
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post) {
        Page<SysPostXEntity> page = getPage();
        QueryWrapper<SysPostXEntity> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(post.getStatus() != null, "status", post.getStatus())
            .eq(post.getPostCode() != null, "post_code", post.getPostCode())
            .eq(post.getPostId() != null, "post_id", post.getPostId())
            .like(StrUtil.isNotEmpty(post.getPostName()), "post_name", post.getPostName());

        postService.page(page, queryWrapper);

        List<SysPost> list = page.getRecords().stream().map(SysPost::new).collect(Collectors.toList());
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public Rdto getInfo(@PathVariable Long postId) {
        return Rdto.success(new SysPost(postService.getById(postId)));
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Rdto add(@Validated @RequestBody SysPost post) {
        if (postService.checkPostNameUnique(null, post.getPostName())) {
            return Rdto.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        if (postService.checkPostCodeUnique(null, post.getPostCode())) {
            return Rdto.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        SysPostXEntity entity = post.toEntity();
        entity.setCreatorId(getUserId());
        entity.setCreateName(getUsername());

        return toAjax(entity.insert());
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Rdto edit(@Validated @RequestBody SysPost post) {
        if (postService.checkPostNameUnique(post.getPostId(), post.getPostName())) {
            return Rdto.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        if (postService.checkPostCodeUnique(post.getPostId(), post.getPostCode())) {
            return Rdto.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }

        SysPostXEntity entity = post.toEntity();


        entity.setUpdaterId(getUserId());
        entity.setUpdateName(getUsername());
        return toAjax(entity.updateById());
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public Rdto remove(@PathVariable Long[] postIds) {

        List<Long> postIdList = Arrays.stream(postIds).collect(Collectors.toList());
        return toAjax(postService.removeBatchByIds(postIdList));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public Rdto optionselect() {
        List<SysPostXEntity> list = postService.list();
        List<SysPost> posts =
            list != null ? list.stream().map(SysPost::new).collect(Collectors.toList()) : new ArrayList<>();
        return Rdto.success(posts);
    }
}
