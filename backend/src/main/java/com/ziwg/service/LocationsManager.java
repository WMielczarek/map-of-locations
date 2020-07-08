package com.ziwg.service;

import com.ziwg.model.db.Location;
import com.ziwg.model.db.LocationsSection;
import com.ziwg.model.db.Project;
import com.ziwg.service.db.ProjectDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LocationsManager {

    private static final String USER_SELECTED_LOCATION_CATEGORY = "nam_loc_user_selected";
    public static final String NOT_LOCATION_CATEGORY = null;

    private final LocationsResolver locationsResolver;
    private final ProjectDao projectDao;

    @Autowired
    public LocationsManager(LocationsResolver locationsResolver, ProjectDao projectDao) {
        this.locationsResolver = locationsResolver;
        this.projectDao = projectDao;
    }

    public Optional<Project> addLocation(Project project, String word) {
        setWordCategory(project, word, LocationsManager.USER_SELECTED_LOCATION_CATEGORY);
        addWordToLocations(project, word);
        Optional<Project> updatedProject = getCoordinates(project);
        projectDao.updateProjectStatus(project.getUser(), project.getProjectName(), Project.Status.READY);
        return updatedProject;
    }

    public Optional<Project> deleteLocation(Project project, String word) {
        setWordCategory(project, word, NOT_LOCATION_CATEGORY);
        removeLocation(project, word);
        return projectDao.updateProject(project);
    }

    public Optional<Project> modifyLocation(Project project, String word, List<Double> newCoordinates) {
        if (!validateCoordinates(newCoordinates)) {
            log.warn("Couldn't modify coordinates. Given values are not correct coordinates");
            return Optional.of(project);
        }
        setNewCoordinates(project, word, newCoordinates);
        return projectDao.updateProject(project);
    }


    private void setWordCategory(Project project, String baseForm, String category) {
        log.info("Setting word category {} for word {} in project {}", USER_SELECTED_LOCATION_CATEGORY, baseForm,
                project.getProjectName());
        project.getSections().forEach(section -> {
            section.getWords().forEach(word -> {
                if (word.getBaseForm().equalsIgnoreCase(baseForm)) {
                    word.setCategory(category);
                }
            });
        });
    }

    private void addWordToLocations(Project project, String word) {
        log.info("Adding word {} to locations list in project {}", word, project.getProjectName());
        List<LocationsSection> newLocationOccurrences = getWordOccurrences(project, word);
        List<LocationsSection> knownLocations = project.getLocationSections();
        for (int i = 0; i < knownLocations.size(); i++) {
            knownLocations.get(i).getLocations().addAll(newLocationOccurrences.get(i).getLocations());
        }
        project.setLocationSections(knownLocations);
    }

    private List<LocationsSection> getWordOccurrences(Project project, String word) {
        List<LocationsSection> allLocations = locationsResolver.filterLocations(project);
        List<LocationsSection> newLocation = filterNewLocation(word, allLocations);
        return groupNewWordOccurrences(newLocation);
    }

    private List<LocationsSection> filterNewLocation(String word, List<LocationsSection> allLocations) {
        allLocations.forEach(section -> {
            List<Location> locations = section.getLocations().stream()
                    .filter(location -> location.getName().equalsIgnoreCase(word))
                    .collect(Collectors.toList());
            section.setLocations(locations);
        });
        return allLocations;
    }

    private List<LocationsSection> groupNewWordOccurrences(List<LocationsSection> allLocations) {
        return allLocations.stream()
                .map(locationsResolver::groupWords)
                .collect(Collectors.toList());
    }

    private Optional<Project> getCoordinates(Project project) {
        log.info("Grouping locations and fetching coordinates from Nominatim for project: {}", project.getProjectName());
        projectDao.updateProjectStatus(project.getUser(), project.getProjectName(), Project.Status.GETTING_LOCATIONS_FROM_NOMINATIM);
        List<LocationsSection> locations = project.getLocationSections().stream()
                .map(locationsResolver::fetchLocations)
                .collect(Collectors.toList());
        project.setLocationSections(locations);
        return projectDao.updateProject(project);
    }

    private void removeLocation(Project project, String word) {
        log.info("Removing location {} from project {}", word, project.getProjectName());
        project.getLocationSections().forEach(section -> {
            List<Location> filteredLocations = section.getLocations().stream()
                    .filter(location -> !location.getName().equalsIgnoreCase(word))
                    .collect(Collectors.toList());
            section.setLocations(filteredLocations);
        });
    }

    private boolean validateCoordinates(List<Double> newCoordinates) {
        return newCoordinates.stream()
                .allMatch(this::isValidCoordinate);
    }

    private boolean isValidCoordinate(Double coordinate) {
        return Double.compare(Math.abs(coordinate), 180.0) < 0;
    }

    private void setNewCoordinates(Project project, String word, List<Double> newCoordinates) {
        log.info("Setting new coordinates for word {} in project {}", word, project.getProjectName());
        project.getLocationSections().forEach(section -> {
            section.getLocations().forEach(location -> {
                if (location.getName().equalsIgnoreCase(word)) {
                    location.getPosition().getGeometry().setCoordinates(newCoordinates);
                }
            });
        });
    }
}
