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

    /**
     * Create database and user with privileges.
     * @param dbName
     * @param usrName
     * @param usrPwd
     */
    @Procedure
    void create_db_user(String dbName, String usrName, String usrPwd);

    /**
     * Find customer by name.
     * @param custName
     * @return
     */
    CustomerConfig findByCustName(String custName);

    /**
     * Execute input SQL script.
     * @param sql
     */
    @Procedure
    void exec_sql(String sql);

}