package com.medical_clinical_app.dao;

import com.medical_clinical_app.model.Patient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class PatientDAO {

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    public Patient save(Patient p) { em.persist(p); return p; }

    public Patient findByUuid(String uuid) {
        return em.createQuery("SELECT p FROM Patient p WHERE p.uuid = :uuid", Patient.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    public Patient update(Patient p) { return em.merge(p); }

    public void delete(Patient p) { em.remove(em.contains(p) ? p : em.merge(p)); }

    public List<Patient> findAll(int page, int size) {
        return em.createQuery("select p from Patient p order by p.idPaciente", Patient.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Patient> searchByNameOrCpf(String q, int page, int size) {
        return em.createQuery("""
                select p from Patient p
                where lower(p.nome) like lower(concat('%', :q, '%'))
                   or p.cpf like concat('%', :q, '%')
                order by p.nome
                """, Patient.class)
                .setParameter("q", q)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}