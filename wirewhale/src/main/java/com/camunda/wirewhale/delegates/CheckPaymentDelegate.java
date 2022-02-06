package com.camunda.wirewhale.delegates;

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
public class CheckPaymentDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        log.info("CheckPaymentDelegate");

        Map<String, Object> map = execution.getVariables();

        Long jobDetailId = (Long) map.get("jobDetailId");
        Boolean paymentReceived = (Boolean) map.get("paymentReceived");

        VariableMap variables = Variables.createVariables();
        variables.put("jobDetailId", jobDetailId);
        variables.put("paymentReceived", paymentReceived != null && paymentReceived);

        execution.setVariables(variables);
    }

}

