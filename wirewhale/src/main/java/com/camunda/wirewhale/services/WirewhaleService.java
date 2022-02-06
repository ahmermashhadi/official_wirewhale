package com.camunda.wirewhale.services;

import camundajar.impl.com.google.gson.Gson;
import camundajar.impl.com.google.gson.reflect.TypeToken;
import com.camunda.wirewhale.db.models.JobDetail;
import com.camunda.wirewhale.db.models.JobDetailPartnerSkill;
import com.camunda.wirewhale.db.models.Partner;
import com.camunda.wirewhale.db.models.PartnerSkill;
import com.camunda.wirewhale.db.repositories.JobDetailRepository;
import com.camunda.wirewhale.db.repositories.PartnerRepository;
import com.camunda.wirewhale.db.repositories.PartnerSkillRepository;
import com.camunda.wirewhale.dtos.mappers.JobDetailMapper;
import com.camunda.wirewhale.dtos.requests.JobDetailsRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WirewhaleService {

    @NonFinal
    @Value("${process.name}")
    String WIRE_WHALE_DIAGRAM;

    TaskService taskService;
    RuntimeService runtimeService;

    JobDetailRepository jobDetailRepository;
    PartnerRepository partnerRepository;
    PartnerSkillRepository partnerSkillRepository;

    JobDetailMapper jobDetailMapper;

    public ResponseEntity startProcessAndReceiveWithConfirmTask(JobDetailsRequest jobDetailsRequest) {

        JobDetail jobDetailForSaved = jobDetailMapper.toJobDetail(jobDetailsRequest);
        jobDetailForSaved.setTimestamp(new Timestamp(new Date().getTime()));
        JobDetail jobDetail = jobDetailRepository.save(jobDetailForSaved);
        VariableMap variables = Variables.createVariables();
        variables.putValue("receive", "receiveTask");
        variables.putValue("jobDetailId", jobDetail.getId());
        runtimeService.startProcessInstanceByKey(WIRE_WHALE_DIAGRAM, variables);

//        ---receive task---
        log.info("receive task");
        Task receiveTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetail.getId())
                .processVariableValueEquals("receive", "receiveTask")
                .singleResult();

        if (receiveTask != null) {
            VariableMap variablesreceiveTask = Variables.createVariables();
            variablesreceiveTask.putValue("jobDetailId", jobDetail.getId());
            variablesreceiveTask.putValue("receivedAllRequiredInformation", "receivedAllRequiredInformation");
            taskService.complete(receiveTask.getId(), variablesreceiveTask);
        }
//        ------------------

        return new ResponseEntity(jobDetail, HttpStatus.OK);
    }

    public ResponseEntity getReceiveTaskCompletion(Long jobDetailId, String jobId) {
//        ---receive task completion---
        log.info("receive task completion");

        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task receiveTaskCompletionTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .processVariableValueEquals("receiveTaskCompletion", "receiveTaskCompletion")
                .singleResult();

        if (receiveTaskCompletionTask != null) {
            VariableMap variables = Variables.createVariables();
            variables.putValue("jobDetailId", jobDetailId);
            variables.putValue("verifyWith2or2", "verifyWith2or2");
            taskService.complete(receiveTaskCompletionTask.getId(), variables);
        } else {
            return new ResponseEntity("Task(completion) didn't find.", HttpStatus.BAD_REQUEST);
        }

//        ---verify with 2or2---
        log.info("verify with 2or2");
        Task verifyWith2or2Task = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .processVariableValueEquals("receive", "receiveTask")
                .singleResult();

        if (verifyWith2or2Task != null) {
            VariableMap variables = Variables.createVariables();
            variables.putValue("jobDetailId", jobDetailId);
            variables.putValue("verifyWith2or2", "verifyWith2or2");
            variables.putValue("receiveVerificationFrom2or2", "receiveVerificationFrom2or2");
            taskService.complete(verifyWith2or2Task.getId(), variables);
        } else {
            return new ResponseEntity("Task(verification) didn't find.", HttpStatus.BAD_REQUEST);
        }
//        ------------------

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity getReceiveVerificationFrom2or2(Long jobDetailId,
                                                         String jobId,
                                                         Boolean taskCompleted) {
//        ---receive verification from 2or2---

        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        log.info("receive verification from 2or2");
        Task receiveTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .processVariableValueEquals("receiveVerificationFrom2or2", "receiveVerificationFrom2or2")
                .singleResult();

        if (receiveTask != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", jobDetailId);
            variables.put("taskCompleted", taskCompleted);
            taskService.complete(receiveTask.getId(), variables);
        } else {
            return new ResponseEntity("Task didn't find.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void sendRequest(String url, Map<String, Object> map) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        // send POST request
        ResponseEntity<Object> response = restTemplate.postForEntity(url, entity, Object.class);

        // check response status code
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Request sent.");
        } else {
            log.info("Request didn't send.");
        }
    }

    public Map<String, Object> getMap(String jobId,
                                      String clientName,
                                      String clientPhone,
                                      Timestamp timestamp,
                                      String status,
                                      String remarks) {
        Map<String, Object> map = new HashMap<>();
        map.put("jobId", jobId);
        map.put("clientName", clientName);
        map.put("clientPhone", clientPhone);
        map.put("timestamp", timestamp);
        map.put("status", status);
        map.put("remarks", remarks);
        return map;
    }

    public void sendInformRequest(String url, String fileName,
                                  Long jobDetailId, String jobId) {

        InputStream fileForByte = getClass().getClassLoader().getResourceAsStream(fileName);
        if (fileForByte == null) {
            throw new IllegalArgumentException("file not found!");
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename("pdfFile")
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileForByte.toString().getBytes(), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    url + "?jobDetailId=" + jobDetailId + "&jobId=" + jobId,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }

        if (response != null && response.getStatusCode() == HttpStatus.OK) {
            log.info("File sent.");
        } else {
            log.info("File did not send.");
        }
    }

    public ResponseEntity getConfirmInform2or2(Long idJobDetail, String jobId) {
        log.info("confirm inform 2or2");

        JobDetail jobDetail = jobDetailRepository.findById(idJobDetail).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task confirmInform2or2Task = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", idJobDetail)
                .processVariableValueEquals("confirmInform2or2", "confirmInform2or2")
                .singleResult();

        if (confirmInform2or2Task != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", idJobDetail);
            variables.put("receiveTaskCompletion", "receiveTaskCompletion");
            taskService.complete(confirmInform2or2Task.getId(), variables);
        } else {
            return new ResponseEntity("Task(confirm inform 2or2) didn't find.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public VariableMap checkPartnerDelegate(Map<String, Object> variablesMap) {

        Long jobDetailId = (Long) variablesMap.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        List<Partner> partnerList = partnerRepository.findAll();

        Gson gson = new Gson();

        List<String> jobDetailPartnerSkillList = new ArrayList<>(100);

        partnerList.forEach(partner -> {
            Double distance = distance(partner.getLatitude(), jobDetail.getLatitude(),
                    partner.getLongitude(), jobDetail.getLongitude());
            if (distance < 150) {
                jobDetailPartnerSkillList.add(gson.toJson(JobDetailPartnerSkill.builder()
                        .partnerId(partner.getId())
                        .jobDetailId(jobDetailId)
                        .alreadyWas(false)
                        .distance(distance)
                        .build()));
            }
        });

        VariableMap variables = Variables.createVariables();
        variables.put("anyPartner", !partnerList.isEmpty());
        variables.put("jobDetailId", jobDetailId);
        variables.put("jobDetailPartnerSkill", jobDetailPartnerSkillList);

        return variables;

    }

    //distance(55.4846, 55.7522, 28.7782, 37.6156, 129, 189);
    public double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 1;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return (Math.sqrt(distance)) / 1000;
    }

    public VariableMap pickPartnerDelegate(Map<String, Object> variablesMap) {

        Long jobDetailId = (Long) variablesMap.get("jobDetailId");

        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        VariableMap variables = Variables.createVariables();
        Type listType = new TypeToken<ArrayList<JobDetailPartnerSkill>>() {
        }.getType();
        Gson gson = new Gson();

        List<JobDetailPartnerSkill> list = gson.fromJson(variablesMap.get("jobDetailPartnerSkill").toString(), listType);
        List<JobDetailPartnerSkill> jobDetailPartnerSkillList = list.stream()
                .filter(jobDetailPartnerSkill -> !jobDetailPartnerSkill.getAlreadyWas())
                .collect(Collectors.toList());

        if (!jobDetailPartnerSkillList.isEmpty()) {
            JobDetailPartnerSkill minJobDetailPartnerSkill = Collections.min(jobDetailPartnerSkillList.stream()
                    .filter(jobDetailPartnerSkill -> !jobDetailPartnerSkill.getAlreadyWas())
                    .collect(Collectors.toList()), Comparator.comparing(JobDetailPartnerSkill::getDistance));

            if (minJobDetailPartnerSkill != null) {
                PartnerSkill partnerSkill =
                        partnerSkillRepository.findAllByPartnerIdAndName(minJobDetailPartnerSkill.getPartnerId(),
                                jobDetail.getJobType());

                if (partnerSkill != null) {
                    Partner partner = partnerRepository.findById(partnerSkill.getPartnerId()).get();
                    log.info("Partner found -> id = {}, phone2 = {}, address = {}, created date = {}",
                            partner.getId(), partner.getPhone2(),
                            partner.getAddress(), partner.getCreatedDate());

                    variables.put("partnerSkill", partnerSkill);
                    variables.put("anyPartner", true);
                    variables.put("jobDetailPartnerSkill", jobDetailPartnerSkillList.stream()
                            .filter(jobDetailPartnerSkill ->
                                    jobDetailPartnerSkill.getPartnerId().equals(partnerSkill.getPartnerId()))
                            .map(gson::toJson)
                            .collect(Collectors.toList()));
                } else {
                    variables.put("anyPartner", false);
                }

            } else {
                variables.put("anyPartner", false);
            }

        } else {
            variables.put("anyPartner", false);
        }

        variables.put("jobDetailId", jobDetailId);

        return variables;
    }

    public ResponseEntity getConfirmPaymentBeforeCheck(Long jobDetailId, String jobId, Boolean confirmPayment) {
        log.info("Confirm payment before check");

        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task confirmInform2or2Task = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .singleResult();

        if (confirmInform2or2Task != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", jobDetailId);
            variables.put("paymentReceived", confirmPayment);
            taskService.complete(confirmInform2or2Task.getId(), variables);
        } else {
            return new ResponseEntity("Task(confirm inform 2or2) didn't find.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public VariableMap getAskForScheduleDelegate(String url, Map<String, Object> variablesMap) {
        Long jobDetailId = (Long) variablesMap.get("jobDetailId");
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail == null) {
            return Variables.createVariables();// new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        sendRequest(url, Map.of("jobDetailId", jobDetail.getId(),
                "jobId", jobDetail.getJobId()));

        Type listType = new TypeToken<ArrayList<JobDetailPartnerSkill>>() {
        }.getType();
        Gson gson = new Gson();
        List<JobDetailPartnerSkill> list = gson.fromJson(variablesMap.get("jobDetailPartnerSkill").toString(), listType);
        JobDetailPartnerSkill jobDetailPartnerSkill = list.stream()
                .min(Comparator.comparingDouble(JobDetailPartnerSkill::getDistance))
                .get();
        List<String> jobDetailPartnerSkillList = list.stream()
                .filter(bean -> !bean.getPartnerId().equals(jobDetailPartnerSkill.getPartnerId()))
                .map(gson::toJson)
                .collect(Collectors.toList());

        VariableMap variables = Variables.createVariables();
        variables.put("jobDetailId", jobDetailId);
        variables.put("confirmReceiveSchedule", "confirmReceiveSchedule");
        variables.put("jobDetailPartnerSkill", jobDetailPartnerSkillList);

        return variables;

    }

    public ResponseEntity getConfirmReceiveSchedule(Long jobDetailId, String jobId) {
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail == null || (jobDetail != null && !jobDetail.getJobId().equals(jobId))) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task confirmReceiveScheduleTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .processVariableValueEquals("confirmReceiveSchedule", "confirmReceiveSchedule")
                .singleResult();

        if (confirmReceiveScheduleTask != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", jobDetailId);
            taskService.complete(confirmReceiveScheduleTask.getId(), variables);
        } else {
            return new ResponseEntity("Task(confirm inform 2or2) didn't find.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity getPartnerConfirmsJobAcceptance(Long jobDetailId, String jobId) {
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task partnerConfirmsJobAcceptanceTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .singleResult();

        if (partnerConfirmsJobAcceptanceTask != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", jobDetailId);
            variables.put("partnerAccepts", true);
            taskService.complete(partnerConfirmsJobAcceptanceTask.getId(), variables);
        } else {
            return new ResponseEntity("Task(Partner confirms the job acceptance) didn't find.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity getAnswerFrom2or2AboutMoreTime(Long jobDetailId, String jobId, Boolean giveMoreTime) {
        JobDetail jobDetail = jobDetailRepository.findById(jobDetailId).get();

        if (jobDetail != null && !jobDetail.getJobId().equals(jobId)) {
            return new ResponseEntity("JobDetail does not exist.", HttpStatus.BAD_REQUEST);
        }

        Task partnerConfirmsJobAcceptanceTask = taskService.createTaskQuery()
                .processDefinitionKey(WIRE_WHALE_DIAGRAM)
                .processVariableValueEquals("jobDetailId", jobDetailId)
                .singleResult();

        if (partnerConfirmsJobAcceptanceTask != null) {
            VariableMap variables = Variables.createVariables();
            variables.put("jobDetailId", jobDetailId);
            variables.put("giveMoreTime", giveMoreTime);
            variables.put("confirmInform2or2", "confirmInform2or2");
            taskService.complete(partnerConfirmsJobAcceptanceTask.getId(), variables);
        } else {
            return new ResponseEntity("Task(Get answer from 2or2 about more time) didn't find.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
