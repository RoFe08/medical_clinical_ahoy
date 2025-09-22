package com.medical_clinical_app.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicineAggregate {
    private Long   medicineId;
    private String medicineName;
    private Long   totalQuantity;
}
