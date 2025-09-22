package com.medical_clinical_app.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientAggregate {
    private Long   patientId;
    private String patientName;
    private Long   totalQuantity;
}

