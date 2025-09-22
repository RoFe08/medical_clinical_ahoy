package com.medical_clinical_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prescription")
    private Long id;

    @Column(updatable = false, unique = true, nullable = false, length = 36)
    private String uuid;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull
    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PrescriptionItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (uuid == null) uuid = UUID.randomUUID().toString();
    }

    // helper para manter ambos os lados sincronizados
    public void addItem(PrescriptionItem item) {
        item.setPrescription(this);
        this.items.add(item);
    }
}
