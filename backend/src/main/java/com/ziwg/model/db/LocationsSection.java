package com.ziwg.model.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class LocationsSection {
    private List<Location> locations;

    public LocationsSection(List<Word> words) {
        this.locations = words.stream()
        .map(Location::new)
        .collect(Collectors.toList());
    }
}
