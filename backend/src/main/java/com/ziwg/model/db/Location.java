package com.ziwg.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ziwg.model.nominatim.NominatimLocationPojo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "name")
public class Location {
    private String name;
    private Long occurrences;
    private NominatimLocationPojo.Feature position;

    public Location(Word word) {
        this.name = word.getBaseForm();
        this.occurrences = 0L;
        this.position = null;
    }

    @JsonIgnore
    public boolean isPositionKnown() {
        return position != null;
    }
}
