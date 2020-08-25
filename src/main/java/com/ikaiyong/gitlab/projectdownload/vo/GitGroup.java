package com.ikaiyong.gitlab.projectdownload.vo;

import lombok.Data;

@Data
public class GitGroup {
    private Long id;
    private String name;
    private String path;
    private String description;
}
