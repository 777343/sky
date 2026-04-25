package com.sky.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/*
文件上传
* */
@Component
public class FileUploadUtil {

    @Value("${sky.file.upload-path}")
    private String uploadPath;
    public String upload(MultipartFile file) throws IOException {

        //生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName= UUID.randomUUID()+suffix;
        //目标文件
        File dest = new File(uploadPath + fileName);
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);

        /*返回可访问路径用于前段回显：需要加上8080，是因为前端nginx开启的是80，前段回显时认为图片在80，其实在8080
        所以需要带上端口号不然默认访问80会失败*/
        return "http://localhost:8080/uploads/"+fileName;

    }
}
