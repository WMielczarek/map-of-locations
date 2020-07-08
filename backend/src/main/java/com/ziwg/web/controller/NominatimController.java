package com.ziwg.web.controller;

import com.ziwg.model.db.Project;
import com.ziwg.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class NominatimController {

    private final ProjectService projectService;

    @Autowired
    public NominatimController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @GetMapping("/locations")
    public ResponseEntity<Object> findLocations(@RequestParam String projectName,
                                                @RequestParam(defaultValue = ClarinController.DEFAULT_USER) String user) {
        Optional<Project> project = projectService.calculateLocations(user, projectName);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get().getId());
        } else {
            return ResponseEntity.badRequest().body("Couldn't find project");
        }
    }
}
