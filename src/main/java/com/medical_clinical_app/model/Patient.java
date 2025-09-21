package com.medical_clinical_app.model;

import com.medical_clinical_app.model.enumeration.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "patients",
        indexes = {
                @Index(name = "ix_patients_nome_ci", columnList = "nome"),
                @Index(name = "ix_patients_ativo",   columnList = "ativo")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(updatable = false, unique = true, nullable = false, length = 36)
    private String uuid;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Size(min = 11, max = 11)
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @NotNull
    @Enumerated(EnumType.STRING)                 // GenderEnum: M, F, O
    @Column(nullable = false, length = 1)        // manter length=1 p/ 'M','F','O'
    private GenderEnum sexo;

    @NotNull
    @Column(name = "dt_inclusao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dtInclusao = LocalDateTime.now();

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
