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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Inform2or2Delegate implements JavaDelegate {

    @NonFinal
    @Value("${api.inform2or2}")
    String url;

    @NonFinal
    @Value("${api.name.inform2or2}")
    String fileName;

    WirewhaleService wirewhaleService;

    JobDetailRepository jobDetailRepository;

    public void execute(DelegateExecution execution) {
        log.info("Inform2or2Delegate");

        Map<String, Object> map = execution.getVariables();

        Long jobDetailId = (Long) map.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        wirewhaleService.sendInformRequest(url, fileName, jobDetail.getId(), jobDetail.getJobId());

        VariableMap variables = Variables.createVariables();
        variables.put("confirmInform2or2", "confirmInform2or2");

        execution.setVariables(variables);
    }

}

