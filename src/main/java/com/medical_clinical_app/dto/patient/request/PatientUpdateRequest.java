package com.medical_clinical_app.dto.patient.request;

import com.medical_clinical_app.model.enumeration.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest implements Serializable {

    private String nome;

    private String cpf;

    private LocalDate dataNascimento;

    private GenderEnum sexo;
}
