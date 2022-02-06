package com.camunda.wirewhale.dtos.mappers;

import com.camunda.wirewhale.db.models.JobDetail;
import com.camunda.wirewhale.dtos.requests.JobDetailsRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobDetailMapper {

    JobDetail toJobDetail(JobDetailsRequest jobDetailsRequest);

}
