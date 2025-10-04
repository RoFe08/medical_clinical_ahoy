package com.medical_clinical_app.dao;

import com.medical_clinical_app.model.Medicine;
import com.medical_clinical_app.model.Patient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class MedicineDAO {

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    public Medicine save(Medicine medicine) {em.persist(medicine); return medicine;}

    public Medicine update(Medicine medicine) {return em.merge(medicine);}

    @Transactional
    public void delete(String uuid) {
        em.createQuery("""
        delete from PrescriptionItem pi
        where pi.medicine.uuid = :u
    """).setParameter("u", uuid).executeUpdate();

        em.createQuery("""
        delete from Medicine m
        where m.uuid = :u
    """).setParameter("u", uuid).executeUpdate();
    }

    public Medicine findByUuid(String uuid) {
        return em.createQuery("SELECT m FROM Medicine m WHERE m.uuid = :uuid", Medicine.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    public List<Medicine> list(String nomeLike, int page, int size) {
        size = Math.min(Math.max(size, 1), 100);

        String filtro = (nomeLike == null || nomeLike.isBlank())
                ? null
                : "%" + nomeLike.trim().toLowerCase() + "%";

        String jpql =
                "SELECT m FROM Medicine m " +
                        "WHERE (:f IS NULL OR LOWER(m.nome) LIKE :f) " +
                        "ORDER BY m.idMedicamento DESC";

        return em.createQuery(jpql, Medicine.class)
                .setParameter("f", filtro)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
