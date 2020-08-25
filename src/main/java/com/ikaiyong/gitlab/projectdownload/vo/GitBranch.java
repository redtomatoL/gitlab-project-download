package com.ikaiyong.gitlab.projectdownload.vo;

import lombok.Data;

@Data
public class GitBranch {
    private String name;
    private GitBranchCommit commit;
}
