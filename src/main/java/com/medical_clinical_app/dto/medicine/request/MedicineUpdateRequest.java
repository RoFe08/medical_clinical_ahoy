package com.medical_clinical_app.dto.medicine.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineUpdateRequest {
    public String  nome;
    public Boolean controlado;
    public String  posologia;
}
