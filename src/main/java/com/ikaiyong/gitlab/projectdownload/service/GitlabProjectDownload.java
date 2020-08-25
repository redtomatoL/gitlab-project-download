package com.ikaiyong.gitlab.projectdownload.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikaiyong.gitlab.projectdownload.vo.GitBranch;
import com.ikaiyong.gitlab.projectdownload.vo.GitGroup;
import com.ikaiyong.gitlab.projectdownload.vo.GitProject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author ljm
 * @description TODO
 * @className GitlabProjectDownload
 * @date 2020/8/24 13:48
 */
@Service
public class GitlabProjectDownload {

    @Autowired
    private OkHttpClient okHttpClient;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${git.gitlabUrl}")
    private String gitlabUrl;

    @Value("${git.privateToken}")
    private String privateToken;

    @Value("${git.projectDir}")
    private String projectDir;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("${git.ignore}")
    public  String ignoreGroup;


    public void start(boolean ifDownLoad) throws IOException {
        File execDir =new File(projectDir);
        FileUtils.cleanDirectory(execDir);
        log.info("清空文件夹{}下的内容",projectDir);
        // 生成目录
        File catalog = new File(new File(projectDir),"目录.txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月研二备份");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy_MM_dd");
        FileUtils.writeStringToFile(catalog,sdf.format(new Date())+"\n","UTF-8",true);

        List<GitGroup> groups = getGroups();
        int i=1,j =1;
        for (GitGroup group : groups) {
            // 组名
            String groupDes = group.getDescription();
            if( Arrays.asList(ignoreGroup.split(",")).contains(group.getName()) ){
                log.info("ignore group, break project组{}",group.getName()+groupDes);
                continue;
            }
            List<GitProject> projects = getProjectsByGroup(group.getId());
            if(projects.size()==0){
                log.info("projects is empty, break project组{}",group.getName()+groupDes);
                continue;
            }
            FileUtils.writeStringToFile(catalog,String.format("%s:%s(%s)\n",i,group.getName(),group.getDescription()),"UTF-8",true);
            i++;
            for (GitProject project : projects) {
                // 项目名
                String projectDes = project.getDescription();
                String lastActivityBranchName = getLastActivityBranchName(project.getId());
                if (StringUtils.isEmpty(lastActivityBranchName)) {
                    log.info("branches is empty, break project组{},项目{}",group.getName()+groupDes,project.getName()+projectDes);
                    continue;
                }
                FileUtils.writeStringToFile(catalog,String.format("      %s、%s(%s)%s\n",j,project.getName(),project.getDescription(),sdf2.format(new Date())), "UTF-8",true);
                j++;
                download(lastActivityBranchName, project,group,ifDownLoad);
            }
        }

        log.info("项目拉取完成");


    }

    private void download(String lastActivityBranchName, GitProject gitProject, GitGroup gitGroup, boolean ifDownLoad) throws IOException {
        String url = String.format("%s/repository/%s/archive.zip?private_token=%s",gitProject.getWebUrl(),lastActivityBranchName,privateToken);

        String pathWithNamespace = String.format("%s(%s)/%s(%s)",gitGroup.getName(),gitGroup.getDescription(),gitProject.getName(),gitProject.getDescription());
        File file = new File(String.format("%s/%s", projectDir, pathWithNamespace.replaceAll("\r|\n", "")));
        FileUtils.forceMkdir(file);


        if(ifDownLoad){
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("Cookie", "_gitlab_session=f62f891a382fcc96a0b5cbaa9c677bcc")
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            File newFile = new File(file,gitProject.getName()+".zip");
            FileUtils.writeByteArrayToFile(newFile,response.body().bytes());
        }

        log.info(pathWithNamespace.replaceAll("\r|\n", "")+"下载完成");



    }

    private String getLastActivityBranchName(Long id) throws IOException {
        List<GitBranch> branches = getBranches(id);
        if (CollectionUtils.isEmpty(branches)) {
            return "";
        }
        GitBranch gitBranch = getLastActivityBranch(branches);
        return gitBranch.getName();
    }

    public GitBranch getLastActivityBranch(final List<GitBranch> gitBranches) {
        GitBranch lastActivityBranch = gitBranches.get(0);
        for (GitBranch gitBranch : gitBranches) {
            if (gitBranch.getCommit().getCommittedDate().getTime() > lastActivityBranch.getCommit().getCommittedDate().getTime()) {
                lastActivityBranch = gitBranch;
            }
        }
        return lastActivityBranch;
    }

    private List<GitBranch> getBranches(Long id) throws IOException {
        String url = gitlabUrl +  String.format("/api/v3/projects/%s/repository/branches?private_token=%s",id,privateToken);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String json = response.body().string();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, GitBranch.class);

        List<GitBranch> list =  objectMapper.readValue(json, javaType);
        return list;
    }

    private List<GitProject> getProjectsByGroup(Long id) throws IOException {
        String url = gitlabUrl + String.format("/api/v3/groups/%s/projects?per_page=%s&private_token=%s",id,100,privateToken);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String json = response.body().string();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, GitProject.class);

        List<GitProject> list =  objectMapper.readValue(json, javaType);
        return list;
    }

    private List<GitGroup> getGroups() throws IOException {


        String url = gitlabUrl + "/api/v3/groups?private_token="+privateToken;

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String json = response.body().string();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, GitGroup.class);

        List<GitGroup> list =  objectMapper.readValue(json, javaType);
        return list;


    }





     /*   public void clone(String branchName, GitProject gitProject, File execDir, String groupDes, String projectDes) {
        String pathWithNamespace = gitProject.getPathWithNamespace();
        String[] split = pathWithNamespace.split("\\/");
        pathWithNamespace = String.format("%s(\"%s\")/%s(\"%s\")",split[0],groupDes,split[1],projectDes);

        String command = String.format("git clone  -b %s %s %s", branchName, gitProject.getHttpUrlToRepo(), pathWithNamespace);
        System.out.println("start exec command : " + command);
        try {
            Process exec = Runtime.getRuntime().exec(command, null, execDir);
//            int i = exec.waitFor();
//            String successResult = StreamUtils.copyToString(exec.getInputStream(), Charset.forName("UTF-8"));
            String errorResult = StreamUtils.copyToString(exec.getErrorStream(),Charset.forName("UTF-8"));
//            System.out.println("successResult: " + successResult);
            log.info(errorResult);
            log.info("================================");
            exec.waitFor();
        } catch (Exception e) {
            log.error("",e);
        }
    }*/

}