package com.urbanbloom.plant.adapter.out.persistence;

import com.urbanbloom.plant.domain.Plant;
import com.urbanbloom.plant.domain.PlantRepository;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlantRepository implements PlantRepository {

    private final Map<String, Plant> plants = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        save(new Plant("Apfelbaum", "Malus domestica", "Ein heimischer Obstbaum.", "temperate", "MEDIUM", "FULL_SUN"));
        save(new Plant("Sonnenblume", "Helianthus annuus", "Einjährige Pflanze mit großen gelben Blüten.", "temperate", "MEDIUM", "FULL_SUN"));
        save(new Plant("Lavendel", "Lavandula angustifolia", "Duftender Halbstrauch.", "mediterranean", "LOW", "FULL_SUN"));
    }

    @Override
    public void save(Plant plant) {
        plants.put(plant.getId(), plant);
    }

    @Override
    public Optional<Plant> findById(String id) {
        return Optional.ofNullable(plants.get(id));
    }

    @Override
    public List<Plant> findAll() {
        return new ArrayList<>(plants.values());
    }
}
