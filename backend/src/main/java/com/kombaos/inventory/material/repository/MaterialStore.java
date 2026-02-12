package com.kombaos.inventory.material.repository;

import com.kombaos.inventory.material.domain.Material;
import java.util.List;
import java.util.Optional;

public interface MaterialStore {
    List<Material> list();

    Optional<Material> getById(String id);

    Material create(String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency);

    Material update(String id, String name, String unit, String supplier, String origin, boolean certified, Long costCents, String currency);

    void delete(String id);
}
