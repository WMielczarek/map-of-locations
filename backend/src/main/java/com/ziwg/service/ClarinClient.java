package com.ziwg.service;

import com.ziwg.model.db.Project;
import com.ziwg.model.db.Section;
import com.ziwg.service.db.ProjectDao;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ClarinClient {

    private static final String START_ANALYZE_URL = "http://ws.clarin-pl.eu/nlprest2/base/startTask/";
    private static final String CHECK_STATUS = "http://ws.clarin-pl.eu/nlprest2/base/getStatus/";
    private static final String DOWNLOAD_RESULT = "http://ws.clarin-pl.eu/nlprest2/base/download/requests/makezip/";

    private final RestTemplate restTemplate;
    private final ProjectDao projectDAO;
    private final FilesReader filesReader;
    private final ProjectService projectService;

    @Autowired
    public ClarinClient(RestTemplate restTemplate, ProjectDao projectDAO, FilesReader filesReader, ProjectService projectService) {
        this.restTemplate = restTemplate;
        this.projectDAO = projectDAO;
        this.filesReader = filesReader;
        this.projectService = projectService;
        this.restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
    }

    public String startAnalyze(String fileHandler, String user, String projectName) throws InvalidParameterException {
        Project project = initializeProjectInDB(user, projectName);
        HttpEntity<String> request = getRequestForAnalyzing(fileHandler, user);
        ResponseEntity<String> response = executeAnalysis(request);
        log.info("Clarin analysis has started for project: {}", projectName);
        projectDAO.updateProjectStatus(project, Project.Status.CLARIN_ANALYSIS);
        return response.getBody();
    }

    public String checkClarinStatus(String processId) {
        return executeStatusCheck(processId).getBody();
    }

    public Optional<Project> getClarinResult(String fileHandler, String user, String projectName) throws IOException {
        Optional<Project> project = Optional.empty();
        if (projectDAO.getProjectByUserAndProjectName(user, projectName) != null) {
            String zipPath = downloadResultZip(fileHandler, user, projectName);
            List<Section> sections = filesReader.getDataFromZip(zipPath);
            project = projectDAO.updateProject(user, projectName, projectService.getWordsNumber(sections), sections);
        }
        return project;
    }

    private Project initializeProjectInDB(String user, String projectName) throws InvalidParameterException {
        return projectDAO.insertProject(user, projectName).orElseThrow(InvalidParameterException::new);
    }

    private HttpHeaders getHeadersForAnalyzing() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private JSONObject getRequestBodyForAnalyzing(String fileHandler, String user) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("lpmn", "filezip(" +fileHandler + ")|any2txt|wcrft2|liner2({\"model\":\"n82\"})|dir|makezip");
        requestBody.put("user", user);
        requestBody.put("application", "literary-map-server");
        return requestBody;
    }

    private HttpEntity<String> getRequestForAnalyzing(String fileHandler, String user) {
        return new HttpEntity<>(
                getRequestBodyForAnalyzing(fileHandler, user).toString(),
                getHeadersForAnalyzing()
        );
    }

    private ResponseEntity<String> executeAnalysis(HttpEntity<String> entity) {
        return restTemplate
                .exchange(START_ANALYZE_URL,
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    private ResponseEntity<String> executeStatusCheck(String processId) {
        return restTemplate.exchange(
                CHECK_STATUS + processId,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class);
    }

    private HttpHeaders getHeadersForFetching() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        return headers;
    }

    private String downloadResultZip(String fileHandler, String user, String projectName) throws IOException {
        HttpEntity<String> request = new HttpEntity<>(getHeadersForFetching());
        log.info("Stared fetching results of analysis from Clarin for project: {}", projectName);
        projectDAO.updateProjectStatus(user, projectName, Project.Status.FETCHING_RESULT_FROM_CLARIN);
        ResponseEntity<byte[]> response = executeFetching(fileHandler, request);
        String zipFullPath = "";
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            zipFullPath = filesReader.saveZip(fileHandler, response.getBody());
        }
        return zipFullPath;
    }

    private ResponseEntity<byte[]> executeFetching(String fileHandler, HttpEntity<String> request) {
        log.info("Fetching result from clarin API");
        return restTemplate.exchange(
                DOWNLOAD_RESULT + fileHandler,
                HttpMethod.GET,
                request,
                byte[].class);
    }
}
