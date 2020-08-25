package com.ikaiyong.gitlab.projectdownload;

import com.ikaiyong.gitlab.projectdownload.service.GitlabProjectDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ProjectdownloadApplication {


	private static Logger log = LoggerFactory.getLogger(ProjectdownloadApplication.class);
	public static void main(String[] args)  {
		SpringApplication.run(ProjectdownloadApplication.class, args);
	}

}
