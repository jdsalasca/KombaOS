package com.kombaos.inventory.material.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialJpaRepository extends JpaRepository<MaterialEntity, String> {
}
