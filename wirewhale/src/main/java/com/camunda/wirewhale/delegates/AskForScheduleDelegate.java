package com.camunda.wirewhale.delegates;

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
public class AskForScheduleDelegate implements JavaDelegate {

    @NonFinal
    @Value("${api.askForSchedule}")
    String url;

    WirewhaleService wirewhaleService;

    public void execute(DelegateExecution execution) {
        log.info("AskForScheduleDelegate");

        Map<String, Object> variablesMap = execution.getVariables();

        execution.setVariables(wirewhaleService.getAskForScheduleDelegate(url, variablesMap));
    }

}

