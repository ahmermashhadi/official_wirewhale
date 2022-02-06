package com.camunda.wirewhale.db.repositories;

import com.camunda.wirewhale.db.models.Partner;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartnerRepository extends CrudRepository<Partner, Long> {

    List<Partner> findAll();

    Optional<Partner> findById(Long partnerId);

}
