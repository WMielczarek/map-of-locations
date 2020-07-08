package com.ziwg.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    private Status status;
    private String user;
    private String projectName;
    private Integer wordsNumber;
    private List<LocationsSection> locationSections;
    @JsonIgnore
    private List<Section> sections;

    public Project(String user, String projectName) {
        this.id = null;
        this.user = user;
        this.projectName = projectName;
        this.wordsNumber = 0;
        this.sections = Collections.emptyList();
        this.locationSections = Collections.emptyList();
        this.status = Status.CREATED;
    }

    public enum Status {
        CREATED,
        CLARIN_ANALYSIS,
        FETCHING_RESULT_FROM_CLARIN,
        FILTERING_LOCATIONS,
        GETTING_LOCATIONS_FROM_NOMINATIM,
        READY
    }
}

