package com.medical_clinical_app.dto.medicine.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {
    public Long idMedicamento;
    public String uuid;
    public String nome;
    public Boolean controlado;
    public String posologia;
    public LocalDateTime dataCadastro;
}