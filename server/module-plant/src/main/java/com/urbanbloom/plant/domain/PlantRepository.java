package com.urbanbloom.plant.domain;

import java.util.List;
import java.util.Optional;

public interface PlantRepository {
    void save(Plant plant);
    Optional<Plant> findById(String id);
    List<Plant> findAll();
}
