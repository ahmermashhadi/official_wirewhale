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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Inform2or2FinishDelegate implements JavaDelegate {

    @NonFinal
    @Value("${api.inform2or2finish}")
    String url;

    @NonFinal
    @Value("${api.name.inform2or2finish}")
    String fileName;

    WirewhaleService wirewhaleService;

    JobDetailRepository jobDetailRepository;

    public void execute(DelegateExecution execution) {
        log.info("Inform2or2FinishDelegate");

        Map<String, Object> map = execution.getVariables();

        Long jobDetailId = (Long) map.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        wirewhaleService.sendInformRequest(url, fileName, jobDetail.getId(), jobDetail.getJobId());
    }

}

