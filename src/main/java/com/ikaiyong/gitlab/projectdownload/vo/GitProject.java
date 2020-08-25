package com.ikaiyong.gitlab.projectdownload.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GitProject {
    private Long id;
    private String name;
    @JsonProperty(value = "default_branch")
    private String defaultBranch;
    @JsonProperty(value = "ssh_url_to_repo")
    private String sshUrlToRepo;
    @JsonProperty(value = "web_url")
    private String webUrl;
    @JsonProperty(value = "http_url_to_repo")
    private String httpUrlToRepo;
    @JsonProperty(value = "path_with_namespace")
    private String pathWithNamespace;
    @JsonProperty(value = "created_at")
    private Date createdAt;
    @JsonProperty(value = "last_activity_at")
    private Date lastActivityAt;
    @JsonProperty(value = "description")
    private String description;
}
