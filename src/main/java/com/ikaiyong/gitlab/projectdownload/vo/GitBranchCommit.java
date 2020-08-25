package com.ikaiyong.gitlab.projectdownload.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GitBranchCommit {
    private String id;
    @JsonProperty(value = "committed_date")
    private Date committedDate;
}
