package com.medical_clinical_app.dto;

import java.time.LocalDateTime;

public class MedicineResponse {
    public Long idMedicamento;
    public String uuid;
    public String nome;
    public Boolean controlado;
    public String posologia;
    public LocalDateTime dataCadastro;
}