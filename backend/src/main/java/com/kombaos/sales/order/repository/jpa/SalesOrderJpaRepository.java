package com.kombaos.sales.order.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderJpaRepository extends JpaRepository<SalesOrderEntity, String> {
}
