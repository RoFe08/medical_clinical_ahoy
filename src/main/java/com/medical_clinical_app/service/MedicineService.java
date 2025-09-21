package com.medical_clinical_app.service;

import com.medical_clinical_app.dto.MedicineCreateRequest;
import com.medical_clinical_app.dto.MedicineResponse;
import com.medical_clinical_app.dto.MedicineUpdateRequest;
import com.medical_clinical_app.model.Medicine;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MedicineService {

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    @Transactional
    public MedicineResponse create(MedicineCreateRequest req) {
        Medicine m = new Medicine();
        m.setNome(req.nome);
        m.setControlado(req.controlado);
        m.setPosologia(req.posologia);
        em.persist(m);
        em.flush();
        return toResponse(m);
    }

    public MedicineResponse findById(Long id) {
        Medicine m = em.find(Medicine.class, id);
        return (m == null) ? null : toResponse(m);
    }

    public List<MedicineResponse> list(String nomeLike, int page, int size) {
        size = Math.min(Math.max(size, 1), 100);

        String jpql = """
            SELECT m FROM Medicine m
            WHERE (:nome IS NULL OR LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
            ORDER BY m.idMedicamento DESC
            """;

        return em.createQuery(jpql, Medicine.class)
                .setParameter("nome", (nomeLike == null || nomeLike.isBlank()) ? null : nomeLike.trim())
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MedicineResponse> listAll(int page, int size) {
        return list(null, page, size);
    }

    @Transactional
    public MedicineResponse update(Long id, MedicineUpdateRequest req) {
        Medicine m = em.find(Medicine.class, id);
        if (m == null) return null;

        if (req.nome != null)        m.setNome(req.nome);
        if (req.controlado != null)  m.setControlado(req.controlado);
        if (req.posologia != null)   m.setPosologia(req.posologia);

        return toResponse(m);
    }

    @Transactional
    public boolean delete(Long id) {
        Medicine m = em.find(Medicine.class, id);
        if (m == null) return false;
        em.remove(m);
        return true;
    }

    @Transactional
    public boolean deleteByUuid(String uuid) {
        List<Medicine> list = em.createQuery(
                        "SELECT m FROM Medicine m WHERE m.uuid = :u", Medicine.class)
                .setParameter("u", uuid)
                .setMaxResults(1)
                .getResultList();
        if (list.isEmpty()) return false;
        em.remove(list.get(0));
        return true;
    }

    private MedicineResponse toResponse(Medicine m) {
        MedicineResponse r = new MedicineResponse();
        r.idMedicamento = m.getIdMedicamento();
        r.uuid          = m.getUuid();
        r.nome          = m.getNome();
        r.controlado    = m.getControlado();
        r.posologia     = m.getPosologia();
        r.dataCadastro  = m.getDataCadastro();
        return r;
    }
}