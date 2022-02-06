package com.camunda.wirewhale.controllers;

import com.camunda.wirewhale.dtos.requests.CommonRequest;
import com.camunda.wirewhale.dtos.requests.JobDetailsRequest;
import com.camunda.wirewhale.dtos.requests.JobStatusRequest;
import com.camunda.wirewhale.exceptions.ExceptionResponse;
import com.camunda.wirewhale.services.WirewhaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ApiResponses(value = {
        @ApiResponse(responseCode = "400",
                description = "Bad Request",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "404",
                description = "Not Found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal Server Error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionResponse.class)))
})
public class WirewhaleController {
    WirewhaleService wirewhaleService;

    @PostMapping("/start/process")
    @Operation(summary = "Start process")
    @ApiResponse(responseCode = "200")
    public ResponseEntity startProcess(@Valid @RequestBody JobDetailsRequest jobDetailsRequest) {
        return wirewhaleService.startProcessAndReceiveWithConfirmTask(jobDetailsRequest);
    }

    @PostMapping("/confirm/inform/2or2")
    @Operation(summary = "Confirm Inform 2or2")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getConfirmInform2or2(@RequestParam Long jobDetailId,
                                               @NotBlank(message = "jobId can't be blank") @RequestParam String jobId) {
        return wirewhaleService.getConfirmInform2or2(jobDetailId, jobId);
    }

    @PostMapping("/receive/task/completion")
    @Operation(summary = "Receive task completion")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getReceiveTaskCompletion(@RequestParam Long jobDetailId,
                                                   @NotBlank(message = "jobId can't be blank") @RequestParam String jobId) {
        return wirewhaleService.getReceiveTaskCompletion(jobDetailId, jobId);
    }

    @PostMapping("/receive/verification")
    @Operation(summary = "Receive verification from 2or2")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getReceiveVerificationFrom2or2(@RequestParam Long jobDetailId,
                                                         @NotBlank(message = "jobId can't be blank") @RequestParam String jobId,
                                                         @RequestParam Boolean taskCompleted) {
        return wirewhaleService.getReceiveVerificationFrom2or2(jobDetailId, jobId, taskCompleted);
    }

    @PostMapping("/confirm/payment/before/check")
    @Operation(summary = "Confirm payment before check")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getConfirmPaymentBeforeCheck(@RequestParam Long jobDetailId,
                                                       @NotBlank(message = "jobId can't be blank") @RequestParam String jobId,
                                                       @RequestParam Boolean confirm) {
        return wirewhaleService.getConfirmPaymentBeforeCheck(jobDetailId, jobId, confirm);
    }

    @PostMapping("/get/answer/from/2or2/about/more/time")
    @Operation(summary = "Get answer from 2or2 about more time")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getAnswerFrom2or2AboutMoreTime(@RequestParam Long jobDetailId,
                                                         @NotBlank(message = "jobId can't be blank") @RequestParam String jobId,
                                                         @RequestParam Boolean giveMoreTime) {
        return wirewhaleService.getAnswerFrom2or2AboutMoreTime(jobDetailId, jobId, giveMoreTime);
    }

    //-----------------------------------------------------------------------------


    //from Partner need
    @PostMapping("/confirm/receive/schedule")
    @Operation(summary = "Confirm receive schedule")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getConfirmReceiveSchedule(@RequestParam Long jobDetailId,
                                                    @RequestParam String jobId) {
        return wirewhaleService.getConfirmReceiveSchedule(jobDetailId, jobId);
    }

    //for Partner need
    @PostMapping("/partner/confirms/job/acceptance")
    @Operation(summary = "Partner confirms the job acceptance")
    @ApiResponse(responseCode = "200")
    public ResponseEntity getPartnerConfirmsJobAcceptance(@RequestParam Long jobDetailId,
                                                          @RequestParam String jobId) {
        return wirewhaleService.getPartnerConfirmsJobAcceptance(jobDetailId, jobId);
    }


    //only for test
    @PostMapping("/test/call/result")
    @Operation(summary = "Test call")
    @ApiResponse(responseCode = "200")
    public ResponseEntity testCall(@RequestBody JobStatusRequest jobStatusRequest) {
        log.info("/test/call/result(jobStatusRequest) -> " + jobStatusRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    //only for test
    @PostMapping(value = "/test/call/document", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Test call")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Object> testCall(@RequestPart MultipartFile file,
                                           @RequestParam Long jobDetailId,
                                           @RequestParam String jobId) {
        log.info("/test/call/document (jobDetailId) -> " + jobDetailId);
        log.info("/test/call/document (jobId) -> " + jobId);
        return ResponseEntity.ok().build();
    }

    //only for test
    @PostMapping(value = "/test/call/ask/for/schedule")
    @Operation(summary = "Test call ask about schedule")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Object> testCallAskAboutSchedule(@RequestBody CommonRequest commonRequest) {
        log.info("/test/call/ask/for/schedule (jobDetailId) -> " + commonRequest.getJobDetailId());
        log.info("/test/call/ask/for/schedule (jobId) -> " + commonRequest.getJobId());
        return ResponseEntity.ok().build();
    }

    //only for test
    @PostMapping(value = "/ask/2or2/give/more/time")
    @Operation(summary = "Ask 2or2 give more time")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Object> testCallAsk2or2GiveMoreTime(@RequestBody CommonRequest commonRequest) {
        log.info("/ask/2or2/give/more/time (jobDetailId) -> " + commonRequest.getJobDetailId());
        log.info("/ask/2or2/give/more/time (jobId) -> " + commonRequest.getJobId());
        return ResponseEntity.ok().build();
    }

    //only for test
    @PostMapping(value = "/test/call/not/enough/data")
    @Operation(summary = "Ask 2or2 give more time")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Object> testCallNotEnoughData(@RequestBody JobStatusRequest jobStatusRequest) {
        log.info("/test/call/not/enough/data (jobId) -> " + jobStatusRequest.getJobId());
        log.info("/test/call/not/enough/data (status) -> " + jobStatusRequest.getStatus());
        return ResponseEntity.ok().build();
    }

}
