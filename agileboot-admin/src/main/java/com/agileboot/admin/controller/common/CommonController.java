package com.agileboot.admin.controller.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.core.domain.ResponseDTO;
import com.agileboot.common.utils.file.FileUploadUtils;
import com.agileboot.infrastructure.config.ServerConfig;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMITER = ",";

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response,
        HttpServletRequest request) {
        try {
            if (!FileUploadUtils.checkAllowDownload(fileName)) {
                throw new Exception(StrUtil.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = AgileBootConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUploadUtils.setAttachmentResponseHeader(response, realFileName);

            IoUtil.copy(FileUtil.getInputStream(filePath), response.getOutputStream());
            if (delete) {
                FileUtil.del(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public ResponseDTO uploadFile(MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = AgileBootConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            ResponseDTO ajax = ResponseDTO.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUploadUtils.getFileNameFromDirPath(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            return ResponseDTO.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public ResponseDTO uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 上传文件路径
            String filePath = AgileBootConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUploadUtils.getFileNameFromDirPath(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            ResponseDTO ajax = ResponseDTO.success();
            ajax.put("urls", StrUtil.join(FILE_DELIMITER, urls));
            ajax.put("fileNames", StrUtil.join(FILE_DELIMITER, fileNames));
            ajax.put("newFileNames", StrUtil.join(FILE_DELIMITER, newFileNames));
            ajax.put("originalFilenames", StrUtil.join(FILE_DELIMITER, originalFilenames));
            return ajax;
        } catch (Exception e) {
            return ResponseDTO.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        try {
            if (!FileUploadUtils.checkAllowDownload(resource)) {
                throw new Exception(StrUtil.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = AgileBootConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StrUtil.subAfter(resource, Constants.RESOURCE_PREFIX, false);
            // 下载名称
            String downloadName = StrUtil.subAfter(downloadPath, "/", false);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUploadUtils.setAttachmentResponseHeader(response, downloadName);
            IoUtil.copy(FileUtil.getInputStream(downloadPath), response.getOutputStream());
        } catch (Exception e) {
            // TODO 失败了 如何更好的提示用户
            log.error("下载文件失败", e);
        }
    }
}
