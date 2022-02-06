package com.camunda.wirewhale.dtos.requests;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommonRequest {
    String jobDetailId;
    String jobId;
}
