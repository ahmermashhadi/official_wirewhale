package com.camunda.wirewhale.delegates;

import com.camunda.wirewhale.db.models.JobDetail;
import com.camunda.wirewhale.db.repositories.JobDetailRepository;
import com.camunda.wirewhale.services.WirewhaleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Ask2or2GiveMoreTimeDelegate implements JavaDelegate {

    @NonFinal
    @Value("${api.ask2or2GiveMoreTime}")
    String url;

    WirewhaleService wirewhaleService;

    JobDetailRepository jobDetailRepository;

    public void execute(DelegateExecution execution) {
        log.info("Ask2or2GiveMoreTimeDelegate");

        Map<String, Object> map = execution.getVariables();

        Long jobDetailId = (Long) map.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        wirewhaleService.sendRequest(url, Map.of("jobDetailId", jobDetail.getId(),
                "jobId", jobDetail.getJobId()));

        VariableMap variables = Variables.createVariables();
        variables.put("jobDetailId", jobDetailId);
        variables.put("getAnswerAboutMoreTime", "getAnswerAboutMoreTime");

        execution.setVariables(variables);
    }

}

