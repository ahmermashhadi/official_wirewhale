package com.camunda.wirewhale.db.repositories;

import com.camunda.wirewhale.db.models.PartnerSkill;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PartnerSkillRepository extends CrudRepository<PartnerSkill, Long> {

    PartnerSkill findAllByPartnerIdAndName(Long partnerIds, String name);

}
