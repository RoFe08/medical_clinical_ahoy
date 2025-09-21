package com.medical_clinical_app.dto.patient.response;

import com.medical_clinical_app.model.enumeration.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private Long idPaciente;

    private String uuid;

    private String nome;

    private String cpf;

    private LocalDate dataNascimento;

    private GenderEnum sexo;

    private LocalDateTime dtInclusao;

    private Boolean ativo;
}
