package com.ikaiyong.gitlab.projectdownload;

import com.ikaiyong.gitlab.projectdownload.service.GitlabProjectDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author ljm
 * @description TODO
 * @className AfterServiceStarted
 * @date 2020/8/25 9:40
 */
@Component
public class AfterServiceStarted implements ApplicationRunner {

    @Value("${git.projectdownload}")
    private String projectdownload;
    @Autowired
    private  GitlabProjectDownload gitlabProjectDownload;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        gitlabProjectDownload.start(StringUtils.endsWithIgnoreCase("1",projectdownload));
    }
}