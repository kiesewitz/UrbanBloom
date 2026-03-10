package com.urbanbloom.plant.domain;

import com.urbanbloom.shared.ddd.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Plant aggregate root representing a species in the catalog.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plant extends AggregateRoot {

    private String nameDe;
    private String scientificName;
    private String description;
    private String climateZone;
    private String waterNeeds;
    private String sunlightNeeds;
    private String imageUrl;

    public Plant(String nameDe, String scientificName, String description, String climateZone, String waterNeeds, String sunlightNeeds) {
        this.nameDe = nameDe;
        this.scientificName = scientificName;
        this.description = description;
        this.climateZone = climateZone;
        this.waterNeeds = waterNeeds;
        this.sunlightNeeds = sunlightNeeds;
    }
}
