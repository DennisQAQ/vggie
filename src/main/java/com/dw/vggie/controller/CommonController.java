package com.dw.vggie.controller;

import com.dw.vggie.common.R;
import com.dw.vggie.dto.DishDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${vggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        String originalName = file.getOriginalFilename();//获取原始文件名称
        String suffix=originalName.substring(originalName.lastIndexOf("."));//截取文件格式；

        String newName = UUID.randomUUID().toString()+suffix;//用UUID为上传的文件重新命名

        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        try{
            file.transferTo(new File(basePath+newName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newName);
    }

    /**
     * 文件回显
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void dawnload(String name, HttpServletResponse response){
        //通过输入流读取文件内容
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //通过输出流将文件返回浏览器展示
            ServletOutputStream outputStream = response.getOutputStream();
            //设置相应头
            response.setContentType("/image/jepg");

            int leng= 0;
            byte [] bytes = new byte[2048];
            while ((leng = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,leng);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
