package com.aloha.freeorder.domain;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Files {

    private String id;
    private String parentTable;
    private String parentId;
    private String name;
    private String originName;
    private String path;
    private long size;
    private boolean isMain;
    private int seq;
    private Date createdAt;
    private Date updatedAt;
    
    private MultipartFile file;
}
