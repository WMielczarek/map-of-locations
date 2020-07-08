package com.ziwg.service.db;

import com.ziwg.model.db.Project;
import com.ziwg.model.db.Section;
import com.ziwg.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProjectDao {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectDao(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> insertProject(String user, String projectName) {
        Optional<Project> newProject = Optional.empty();
        if (getProjectByUserAndProjectName(user, projectName) == null) {
            log.info("Inserting new project with name {} by user {}.", projectName, user);
            newProject = Optional.of(projectRepository.save(new Project(user, projectName)));
        }
        return newProject;
    }

    public Optional<Project> updateProject(Project project) {
        Optional<Project> updatedProject = projectRepository.findByUserAndProjectName(project.getUser(), project.getProjectName());
        if (updatedProject.isPresent()) {
            log.info("Updating project with name {}", project.getProjectName());
            project.setId(updatedProject.get().getId());
            updatedProject = Optional.of(projectRepository.save(project));
        }
        return updatedProject;
    }

    public Optional<Project> updateProject(String user, String projectName, int wordsNumber, List<Section> sections) {
        Optional<Project> updatedProject = projectRepository.findByUserAndProjectName(user, projectName);
        if (updatedProject.isPresent()) {
            log.info("Updating project with name {}", projectName);
            updatedProject.get().setSections(sections);
            updatedProject.get().setWordsNumber(wordsNumber);
            updatedProject = Optional.of(projectRepository.save(updatedProject.get()));
        }
        return updatedProject;
    }

    public Project getProjectByUserAndProjectName(String user, String projectName) {
        return projectRepository.findByUserAndProjectName(user, projectName).orElse(null);
    }

    public List<Project> getAllProjects() {
        log.info("Getting all projects from DB");
        return projectRepository.findAll();
    }

    public Optional<Project> updateProjectStatus(String user, String projectName, Project.Status status) {
        Project project = getProjectByUserAndProjectName(user, projectName);
        if (project != null) {
            return updateProjectStatus(project, status);
        }
        return Optional.empty();
    }

    public Optional<Project> updateProjectStatus(Project project, Project.Status status) {
        log.info("Setting status ({}) for project: {}", status, project.getProjectName());
        project.setStatus(status);
        return updateProject(project);
    }

}
