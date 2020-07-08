package com.ziwg.model.nominatim;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "unused", "rawtypes"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NominatimLocationPojo {

    private List<Feature> features;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Feature {
        private String type;
        private Geometry geometry;

        @AllArgsConstructor
        @NoArgsConstructor
        @Setter
        public static class Geometry {
            private String type;
            private Object coordinates;

            public String getType() {
                return type;
            }

            /**
            * This application supports only Point location object from Nominatim.
            * Basing on Nominatim docs, when type is "Point", then it should return list with two Double values.
            **/
            public List<Double> getCoordinates() {
                if (type.equals("Point") && coordinates instanceof List) {
                    return (List<Double>) ((List) coordinates).stream()
                            .map(this::validateCoordinateTypes)
                            .collect(Collectors.toList());
                } else return Collections.emptyList();

            }

            private Object validateCoordinateTypes(Object coordinate) {
                if (coordinate instanceof Double) return coordinate;
                else if (coordinate instanceof Integer) return Double.valueOf((Integer) coordinate);
                else return 0.0;
            }
        }
    }
}
