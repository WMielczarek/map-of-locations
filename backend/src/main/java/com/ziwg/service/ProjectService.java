package com.ziwg.service;

import com.ziwg.model.db.Project;
import com.ziwg.model.db.Section;
import com.ziwg.service.db.ProjectDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ProjectService {

    private final LocationsResolver locationsResolver;
    private final ProjectDao projectDao;
    private final LocationsManager locationsManager;

    @Autowired
    public ProjectService(LocationsResolver locationsResolver, ProjectDao projectDao, LocationsManager locationsManager) {
        this.locationsResolver = locationsResolver;
        this.projectDao = projectDao;
        this.locationsManager = locationsManager;
    }

    public Optional<Project> calculateLocations(String user, String projectName) {
        Project project = projectDao.getProjectByUserAndProjectName(user, projectName);
        if (project != null) {
            return locationsResolver.resolveLocations(project);
        } else return Optional.empty();
    }

    public Optional<Project> addLocation(String user, String projectName, String word) {
        Project project = projectDao.getProjectByUserAndProjectName(user, projectName);
        if (project != null && !isWordLocation(project, word)) {
            return locationsManager.addLocation(project, word);
        } else if (project != null) {
            log.warn("Word {} is already recognized as location in project {}", word, projectName);
            return Optional.of(project);
        } else
            return Optional.empty();
    }

    public Optional<Project> deleteLocation(String user, String projectName, String word) {
        Project project = projectDao.getProjectByUserAndProjectName(user, projectName);
        if (project != null && isWordLocation(project, word)) {
            return locationsManager.deleteLocation(project, word);
        } else if (project != null) {
            log.warn("Word {} is not recognized as location in project {}", word, projectName);
            return Optional.of(project);
        } else
            return Optional.empty();
    }

    public Optional<Project> modifyLocation(String user, String projectName, String word, List<Double> coordinates) {
        Project project = projectDao.getProjectByUserAndProjectName(user, projectName);
        if (project != null && isWordLocation(project, word)) {
            return locationsManager.modifyLocation(project, word, coordinates);
        } else if (project != null) {
            log.warn("Word {} is not recognized as location in project {}", word, projectName);
            return Optional.of(project);
        } else
            return Optional.empty();
    }

    public int getWordsNumber(List<Section> sections) {
        return sections.stream()
                .mapToInt(it -> it.getWords().size())
                .sum();
    }

    public boolean doesProjectExist(Project project) {
        return project != null;
    }

    private boolean isWordLocation(Project project, String word) {
        return project.getLocationSections().stream()
                .anyMatch(section -> section.getLocations().stream()
                        .anyMatch(location -> location.getName().equalsIgnoreCase(word))
                );
    }
}
