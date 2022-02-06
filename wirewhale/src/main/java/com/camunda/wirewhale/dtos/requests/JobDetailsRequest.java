package com.camunda.wirewhale.dtos.requests;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobDetailsRequest {
    @NotBlank(message = "jobId can't be blank")
    String jobId;
    @NotBlank(message = "clientName can't be blank")
    String clientName;
    @NotBlank(message = "clientPhone can't be blank")
    String clientPhone;
    @NotBlank(message = "jobType can't be blank")
    String jobType;
    @NotBlank(message = "address can't be blank")
    String address;
    @NotBlank(message = "city can't be blank")
    String city;
    @NotNull(message = "longitude can't be null")
    Double longitude;
    @NotNull(message = "latitude can't be null")
    Double latitude;
}
