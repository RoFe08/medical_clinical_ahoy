package com.medical_clinical_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long idMedicamento;

    @Column(updatable = false, unique = true, nullable = false, length = 36)
    private String uuid;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nome;

    @NotNull
    @Column(nullable = false)
    private Boolean controlado; // true = Sim, false = Não

    @Column(length = 255)
    private String posologia;

    @NotNull
    @Column(nullable = false, updatable = false, name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();

//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "manufacture_id", nullable = false)
//    private Manufacturer Manufacturer;

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
