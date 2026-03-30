package com.dentflow.core.clinic.infrastructure;

import com.dentflow.core.clinic.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
