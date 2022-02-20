package com.sportologic.sprtadmin.repository;

import com.sportologic.common.model.domain.CustomerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository. Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("customerConfigRepository")
public interface CustomerConfigRepository extends JpaRepository<CustomerConfig, UUID> {

    /* JPQL query */
    @Query("SELECT c FROM CustomerConfig c")
    /* Native query.
    @Query(
            value = "SELECT * FROM customer_config",
            nativeQuery = true) */
    List<CustomerConfig> findAllCustom();

    @Procedure
    void create_db(String dbName);
}
