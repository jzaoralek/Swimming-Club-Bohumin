package com.sportologic.sprtadmin.repository;

import com.sportologic.common.model.domain.CustomerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("customerConfigRepository")
public interface CustomerConfigRepository extends JpaRepository<CustomerConfig, UUID> {
}
