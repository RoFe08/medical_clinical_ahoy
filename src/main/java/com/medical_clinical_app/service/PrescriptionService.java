package com.medical_clinical_app.service;

import com.medical_clinical_app.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class PrescriptionService {

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    @Transactional
    public String createByUuids(String patientUuid, LocalDate prescriptionDate,
                                List<ItemInput> items) {

        if (patientUuid == null || patientUuid.isBlank())
            throw new IllegalArgumentException("Paciente obrigatório");
        if (prescriptionDate == null)
            throw new IllegalArgumentException("Data da prescrição obrigatória");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Inclua pelo menos 1 medicamento");

        Set<String> seen = new HashSet<>();
        for (ItemInput it : items) {
            if (it.medicineUuid == null || it.medicineUuid.isBlank())
                throw new IllegalArgumentException("UUID do medicamento é obrigatório");
            if (!seen.add(it.medicineUuid))
                throw new IllegalArgumentException("Medicamento repetido na receita");
            if (it.quantity == null || it.quantity < 1)
                throw new IllegalArgumentException("Quantidade inválida");
        }

        Patient patient = em.createQuery(
                        "SELECT p FROM Patient p WHERE p.uuid = :u", Patient.class)
                .setParameter("u", patientUuid)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        Prescription rx = Prescription.builder()
                .patient(patient)
                .prescriptionDate(prescriptionDate)
                .build();

        for (ItemInput in : items) {
            Medicine med = em.createQuery(
                            "SELECT m FROM Medicine m WHERE m.uuid = :u", Medicine.class)
                    .setParameter("u", in.medicineUuid)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado: " + in.medicineUuid));

            rx.addItem(PrescriptionItem.builder()
                    .medicine(med)
                    .quantity(in.quantity)
                    .build());
        }

        em.persist(rx);
        em.flush(); // garante UUID gerado
        return rx.getUuid();
    }

    public static class ItemInput {
        public String medicineUuid;
        public Integer quantity;

        public ItemInput() {}
        public ItemInput(String uuid, Integer q) { this.medicineUuid = uuid; this.quantity = q; }
    }
}
