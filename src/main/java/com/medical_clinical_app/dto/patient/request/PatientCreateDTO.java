package com.medical_clinical_app.dto.patient.request;

import com.medical_clinical_app.model.enumeration.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PatientCreateDTO implements Serializable {

    @NotBlank
    private String nome;

    @NotBlank
    private String cpf;

    @NotNull
    private LocalDate dataNascimento;

    @NotNull
    private GenderEnum sexo;
}
