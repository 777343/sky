package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
通用接口：文件上传
* */
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){

        log.info("文件上传：{}",file);
        try {
            String uploadPath = fileUploadUtil.upload(file);
            return Result.success(uploadPath);
        } catch (IOException e) {

           log.error("文件上传失败：{}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
