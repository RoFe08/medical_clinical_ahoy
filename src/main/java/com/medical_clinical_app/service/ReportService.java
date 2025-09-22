package com.medical_clinical_app.service;

import com.medical_clinical_app.dto.report.MedicineAggregate;
import com.medical_clinical_app.dto.report.PatientAggregate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class ReportService {

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    public List<MedicineAggregate> top2MedicinesByQuantity() {
        return em.createQuery(
                        "SELECT new com.medical_clinical_app.dto.report.MedicineAggregate(" +
                                "  m.idMedicamento, m.nome, COALESCE(SUM(i.quantity), 0) ) " +
                                "FROM Prescription pres " +
                                "JOIN pres.items i " +
                                "JOIN i.medicine m " +
                                "GROUP BY m.idMedicamento, m.nome " +
                                "ORDER BY SUM(i.quantity) DESC",
                        MedicineAggregate.class
                )
                .setMaxResults(2)
                .getResultList();
    }

    public List<PatientAggregate> top2PatientsByQuantity() {
        return em.createQuery(
                        "SELECT new com.medical_clinical_app.dto.report.PatientAggregate(" +
                                "  p.idPaciente, p.nome, COALESCE(SUM(i.quantity), 0) ) " +
                                "FROM Prescription pres " +
                                "JOIN pres.patient p " +
                                "JOIN pres.items i " +
                                "GROUP BY p.idPaciente, p.nome " +
                                "ORDER BY SUM(i.quantity) DESC",
                        PatientAggregate.class
                )
                .setMaxResults(2)
                .getResultList();
    }

    public List<MedicineAggregate> totalsByMedicineForPatient(Long patientId) {
        return em.createQuery(
                        "SELECT new com.medical_clinical_app.dto.report.MedicineAggregate(" +
                                "  m.idMedicamento, m.nome, COALESCE(SUM(i.quantity), 0) ) " +
                                "FROM Prescription pres " +
                                "JOIN pres.patient p " +
                                "JOIN pres.items i " +
                                "JOIN i.medicine m " +
                                "WHERE p.idPaciente = :pid " +
                                "GROUP BY m.idMedicamento, m.nome " +
                                "ORDER BY m.nome ASC",
                        MedicineAggregate.class
                )
                .setParameter("pid", patientId)
                .getResultList();
    }

    public Long totalQuantityForPatient(Long patientId) {
        return em.createQuery(
                        "SELECT COALESCE(SUM(i.quantity), 0) " +
                                "FROM Prescription pres " +
                                "JOIN pres.patient p " +
                                "JOIN pres.items i " +
                                "WHERE p.idPaciente = :pid",
                        Long.class
                )
                .setParameter("pid", patientId)
                .getSingleResult();
    }
}