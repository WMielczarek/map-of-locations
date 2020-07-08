package com.ziwg.service;

import com.ziwg.model.db.Location;
import com.ziwg.model.db.LocationsSection;
import com.ziwg.model.db.Project;
import com.ziwg.model.db.Section;
import com.ziwg.model.db.Word;
import com.ziwg.model.nominatim.NominatimLocationPojo;
import com.ziwg.service.db.ProjectDao;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class LocationsResolver {

    private final ProjectDao projectDao;
    private final NominatimClient nominatimClient;

    @Autowired
    public LocationsResolver(ProjectDao projectDao, NominatimClient nominatimClient) {
        this.projectDao = projectDao;
        this.nominatimClient = nominatimClient;
    }

    public Optional<Project> resolveLocations(Project project) {
        List<LocationsSection> locations = filterLocations(project);
        project = saveLocations(project, locations);
        log.info("Locations analysis finished");
        return projectDao.updateProjectStatus(project, Project.Status.READY);
    }

    List<LocationsSection> filterLocations(Project project) {
        log.info("Filtering locations from project: {}", project.getProjectName());
        projectDao.updateProjectStatus(project.getUser(), project.getProjectName(), Project.Status.FILTERING_LOCATIONS);
        return project.getSections().stream()
                .map(this::filterLocationsInSection)
                .map(LocationsSection::new)
                .collect(Collectors.toList());
    }

    private Project saveLocations(Project project, List<LocationsSection> locations) {
        log.info("Grouping locations and fetching coordinates from Nominatim for project: {}", project.getProjectName());
        projectDao.updateProjectStatus(project.getUser(), project.getProjectName(), Project.Status.GETTING_LOCATIONS_FROM_NOMINATIM);
        locations = locations.stream()
                .map(this::groupWords)
                .map(this::fetchLocations)
                .collect(Collectors.toList());
        project.setLocationSections(locations);
        return projectDao.updateProject(project).orElse(null);
    }

    private List<Word> filterLocationsInSection(Section section) {
        return section.getWords().stream()
                .filter(Word::isLocation)
                .collect(Collectors.toList());
    }

    LocationsSection groupWords(LocationsSection locations) {
        countOccurrences(locations);
        filterUniqueLocations(locations);
        return locations;
    }

    private void filterUniqueLocations(LocationsSection locations) {
        locations.setLocations(locations.getLocations().stream()
                .distinct()
                .collect(Collectors.toList()));
    }

    private void countOccurrences(LocationsSection locations) {
        locations.getLocations().forEach(location -> {
            long occurrences = Collections.frequency(locations.getLocations(), location);
            location.setOccurrences(occurrences);
        });
    }

    LocationsSection fetchLocations(LocationsSection locationsSection) {
        locationsSection.getLocations().forEach(location -> {
            NominatimLocationPojo.Feature position = getCoordinates(location);
            location.setPosition(position);
        });
        return locationsSection;
    }

    private NominatimLocationPojo.Feature getCoordinates(Location location) {
        NominatimLocationPojo.Feature coordinates = location.getPosition();
        try {
            if (!location.isPositionKnown()) {
                coordinates = nominatimClient.getLocation(location.getName());
                TimeUnit.MILLISECONDS.sleep(NominatimClient.NOMINATIM_REQUEST_INTERVAL);
            }
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        }
        return coordinates;
    }

}
