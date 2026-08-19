package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    //阿里云OSS文件存储工具
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传（上传到阿里云OSS对象存储服务平台）
     * @param file 待上传文件
     * @return业务处理结果
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            //原始文件名
            String originalFileName = file.getOriginalFilename();
            //原始文件名后缀
            String extention = originalFileName.substring(originalFileName.lastIndexOf('.'));
            //通过UUID构造新的文件名称（防止重复）
            String objectName = UUID.randomUUID().toString() + extention;
            //获取文件请求路径并上传至阿里云OSS
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
