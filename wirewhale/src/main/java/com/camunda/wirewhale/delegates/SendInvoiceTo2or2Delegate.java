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
public class SendInvoiceTo2or2Delegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        log.info("SendInvoiceTo2or2Delegate");

        VariableMap variables = Variables.createVariables();
        variables.put("timer1MinForBeforeCheck", "timer1MinForBeforeCheck");

        execution.setVariables(variables);
    }

}

