package com.camunda.wirewhale.delegates;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Test3Delegate implements JavaDelegate {

//    CamundaDelegateService camundaDelegateService;

    public void execute(DelegateExecution execution) throws Exception {
        log.info("Test3Delegate");

//        Long employeeId = (Long) execution.getVariable(EMPLOYEE_ID);
//        Long contractStatusId = (Long) execution.getVariable(CONTRACT_STATUS_ID);
//
//        camundaDelegateService.completeContract(employeeId, contractStatusId).subscribe();

//        log.info("The Task {} has been checked!", execution.getId());
    }

}

