package com.aloha.freeorder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aloha.freeorder.domain.Files;
import com.aloha.freeorder.service.FileService;
import com.aloha.freeorder.util.MediaUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("")
public class FileController {
    @Autowired private FileService fileDAO;

    @GetMapping("/img")
    public void showImg(@RequestParam("id") Long id
                        ,HttpServletResponse response) throws Exception {
        log.info("[FILE] id : " + id);

        Files fileInfo  = fileDAO.select(id);
        String filePath = fileInfo.getPath() + "\\" + fileInfo.getName();

        File file = new File(filePath);

        FileInputStream fis = new FileInputStream(file);
        ServletOutputStream sos = response.getOutputStream();

        FileCopyUtils.copy(fis, sos);   // 입력한 파일 출력

        // 확장자로 컨텐츠 타입 지정
        // - 확장자 : jpg, png, ....
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1); // 확장자
        MediaType mediaType = MediaUtil.getMediaType(ext);

        if ( mediaType == null ) return;
        log.info("mediaType : " + mediaType);
        response.setContentType( mediaType.toString() ); //image/jpeg
    }

    @ResponseBody
    @DeleteMapping("/files/{id}")
    public String getMethodName(@PathVariable("id") Long id) throws Exception {
        Files fileInfo  = fileDAO.select(id);
        String filePath = fileInfo.getPath() + "\\" + fileInfo.getName();
        File delFile = new File(filePath);
        if(delFile.exists())
            delFile.delete();

        int result = fileDAO.delete(fileInfo.getId());
        if (result > 0) 
            return "SUCCESS";
        else
            return "FAIL";
    }
    
    @ResponseBody
    @GetMapping("/files/{no}")
    public ResponseEntity<?> fileList(@RequestParam("no") Long no) throws Exception {
        List<Files> fileList = fileDAO.list(no, "board");
        return new ResponseEntity<>(fileList,HttpStatus.OK);
    }
}