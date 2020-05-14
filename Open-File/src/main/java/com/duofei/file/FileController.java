package com.duofei.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传与下载控制器
 * @author duofei
 * @date 2020/5/14
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public void upload(MultipartFile file){
        logger.info("FILE-> name:{}, contentType:{}， size:{}", file.getName(), file.getContentType(), file.getSize());
    }
}
