package com.ziwg.web.controller;

import com.ziwg.model.db.Project;
import com.ziwg.service.ProjectService;
import com.ziwg.service.db.ProjectDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/api")
public class ProjectController {

    public static final String PROJECT_NOT_FOUND_MSG = "Couldn't find project.";

    private final ProjectService projectService;
    private final ProjectDao projectDAO;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectDao projectDAO) {
        this.projectService = projectService;
        this.projectDAO = projectDAO;
    }

    @GetMapping("/project")
    public ResponseEntity<Object> getProject(@RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user,
                                             @RequestParam(required = false) String projectName) {

        Project project = projectDAO.getProjectByUserAndProjectName(user, projectName);
        if (projectService.doesProjectExist(project)) {
            return ResponseEntity.ok(project);
        } else {
            log.warn(PROJECT_NOT_FOUND_MSG);
            return ResponseEntity.badRequest().body(PROJECT_NOT_FOUND_MSG);
        }
    }

    @GetMapping("/project/status")
    public ResponseEntity<Object> getStatus(@RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user,
                                            @RequestParam(required = false) String projectName) {

        Project.Status status = projectDAO.getProjectByUserAndProjectName(user, projectName).getStatus();
        if (status != null) {
            return ResponseEntity.ok(status);
        } else {
            log.warn(PROJECT_NOT_FOUND_MSG);
            return ResponseEntity.badRequest().body(PROJECT_NOT_FOUND_MSG);
        }
    }

    @GetMapping("/project/add")
    public ResponseEntity<Object> addLocation(@RequestParam String word,
                                              @RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user,
                                              @RequestParam String projectName) {

        Optional<Project> project = projectService.addLocation(user, projectName, word);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get().getId());
        } else {
            log.warn(PROJECT_NOT_FOUND_MSG);
            return ResponseEntity.badRequest().body(PROJECT_NOT_FOUND_MSG);
        }
    }

    @GetMapping("/project/delete")
    public ResponseEntity<Object> deleteLocation(@RequestParam String word,
                                                 @RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user,
                                                 @RequestParam String projectName) {

        Optional<Project> project = projectService.deleteLocation(user, projectName, word);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get().getId());
        } else {
            log.warn(PROJECT_NOT_FOUND_MSG);
            return ResponseEntity.badRequest().body(PROJECT_NOT_FOUND_MSG);
        }
    }

    @GetMapping("/project/modify")
    public ResponseEntity<Object> modifyLocation(@RequestParam String word,
                                                 @RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user,
                                                 @RequestParam String projectName,
                                                 @RequestParam List<Double> coordinates) {
        Optional<Project> project = projectService.modifyLocation(user, projectName, word, coordinates);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get().getId());
        } else {
            log.warn(PROJECT_NOT_FOUND_MSG);
            return ResponseEntity.badRequest().body(PROJECT_NOT_FOUND_MSG);
        }
    }

}
