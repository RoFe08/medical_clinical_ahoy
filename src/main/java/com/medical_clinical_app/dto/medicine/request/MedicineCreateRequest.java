package com.medical_clinical_app.dto.medicine.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicineCreateRequest {
    @NotBlank
    public String nome;
    @NotNull
    public Boolean controlado;
    public String  posologia;
}