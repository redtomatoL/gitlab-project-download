package com.ikaiyong.gitlab.projectdownload;

import com.ikaiyong.gitlab.projectdownload.service.GitlabProjectDownload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ProjectdownloadApplicationTests {

	@Autowired
	private GitlabProjectDownload gitlabProjectDownload;

	@Test
	void downloadProject() throws IOException {
		gitlabProjectDownload.start(false);
	}






}
