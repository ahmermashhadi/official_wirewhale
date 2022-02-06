package com.camunda.wirewhale.delegates;

import com.camunda.wirewhale.db.models.JobDetail;
import com.camunda.wirewhale.db.models.enums.Skill;
import com.camunda.wirewhale.db.repositories.JobDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReceivedAllRequiredInformationDelegate implements JavaDelegate {

    JobDetailRepository jobDetailRepository;

    public void execute(DelegateExecution execution) {
        log.info("ReceivedAllRequiredInformationDelegate");

        Map<String, Object> map = execution.getVariables();

        Long jobDetailId = (Long) map.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        VariableMap variables = Variables.createVariables();
        Boolean jobTypeRight = jobDetail.getJobType().equals(Skill.Internet.name()) ||
                jobDetail.getJobType().equals(Skill.Telephone.name());

        log.info("jobTypeRight -> " + jobDetail.getJobType() + ", " + jobTypeRight);

        variables.put("allInformation", jobTypeRight);

        execution.setVariables(variables);
    }

}

