package com.camunda.wirewhale.delegates;

import com.camunda.wirewhale.services.WirewhaleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PickPartnerDelegate implements JavaDelegate {

    WirewhaleService wirewhaleService;

    public void execute(DelegateExecution execution) {
        log.info("PickPartnerDelegate");

        Map<String, Object> variablesMap = execution.getVariables();

        execution.setVariables(wirewhaleService.pickPartnerDelegate(variablesMap));
    }
}
