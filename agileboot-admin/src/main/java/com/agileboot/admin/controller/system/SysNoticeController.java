package com.agileboot.admin.controller.system;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.admin.deprecated.domain.SysNotice;
import com.agileboot.common.core.controller.BaseController;
import com.agileboot.common.core.dto.PageDTO;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.common.enums.BusinessType;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.util.AuthenticationUtils;
import com.agileboot.orm.entity.SysNoticeXEntity;
import com.agileboot.orm.service.ISysNoticeXService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
 * 公告 信息操作处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {

    @Autowired
    private ISysNoticeXService noticeService;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(SysNotice notice) {
        Page<SysNoticeXEntity> page = getPage();
        QueryWrapper<SysNoticeXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.like(StrUtil.isNotEmpty(notice.getNoticeTitle()), "notice_title", notice.getNoticeTitle())
                .eq(notice.getNoticeType()!=null, "notice_type" , notice.getNoticeType())
                    .like(notice.getCreateBy()!=null, "creator_name", notice.getCreateBy());
        noticeService.page(page, sysNoticeWrapper);
        return ResponseDTO.ok(new PageDTO(page));
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public ResponseDTO getInfo(@PathVariable Long noticeId) {
        return ResponseDTO.ok(noticeService.getById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @AccessLog(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public ResponseDTO add(@Validated @RequestBody SysNotice notice) {

        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        SysNoticeXEntity sysNoticeXEntity = new SysNoticeXEntity();
        sysNoticeXEntity.setNoticeTitle(notice.getNoticeTitle());
        sysNoticeXEntity.setNoticeType(Convert.toInt(notice.getNoticeType()));
        sysNoticeXEntity.setNoticeContent(notice.getNoticeContent());
        sysNoticeXEntity.setCreatorId(loginUser.getUserId());
        sysNoticeXEntity.setCreatorName(loginUser.getUsername());
        sysNoticeXEntity.insert();
        return ResponseDTO.ok();
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @AccessLog(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public ResponseDTO edit(@Validated @RequestBody SysNotice notice) {

        LoginUser loginUser = AuthenticationUtils.getLoginUser();
        SysNoticeXEntity sysNoticeXEntity = new SysNoticeXEntity();
        sysNoticeXEntity.setNoticeId(notice.getNoticeId().intValue());
        sysNoticeXEntity.setNoticeTitle(notice.getNoticeTitle());
        sysNoticeXEntity.setNoticeType(Convert.toInt(notice.getNoticeType()));
        sysNoticeXEntity.setNoticeContent(notice.getNoticeContent());
        sysNoticeXEntity.setUpdaterId(loginUser.getUserId());
        sysNoticeXEntity.setUpdaterName(loginUser.getUsername());
        sysNoticeXEntity.updateById();
        return ResponseDTO.ok();
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @AccessLog(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public ResponseDTO remove(@PathVariable Long[] noticeIds) {
        QueryWrapper<SysNoticeXEntity> sysNoticeWrapper = new QueryWrapper<>();
        sysNoticeWrapper.in("notice_id", noticeIds);
        noticeService.remove(sysNoticeWrapper);
        return ResponseDTO.ok();
    }
}
