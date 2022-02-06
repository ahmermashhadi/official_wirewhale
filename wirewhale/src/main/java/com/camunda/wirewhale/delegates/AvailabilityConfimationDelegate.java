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

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AvailabilityConfimationDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        log.info("AvailabilityConfimationDelegate");

        VariableMap variables = Variables.createVariables();
        variables.put("available", true);

        execution.setVariables(variables);
    }

}

