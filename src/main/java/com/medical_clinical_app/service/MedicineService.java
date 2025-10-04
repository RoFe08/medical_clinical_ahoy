package com.medical_clinical_app.service;

import com.medical_clinical_app.dao.MedicineDAO;
import com.medical_clinical_app.dto.medicine.request.MedicineCreateRequest;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;
import com.medical_clinical_app.dto.medicine.request.MedicineUpdateRequest;
import com.medical_clinical_app.model.Medicine;

import com.medical_clinical_app.model.Patient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MedicineService {

    @Inject
    private MedicineDAO medicineDAO;

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
        return entityToMedicineDTO(m);
    }

    public MedicineResponse findById(Long id) {
        Medicine m = em.find(Medicine.class, id);
        return (m == null) ? null : entityToMedicineDTO(m);
    }

    public List<MedicineResponse> listAll(String nome, int page, int size) {
        return medicineDAO.list(null, page, size)
                .stream()
                .map(this::entityToMedicineDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public MedicineResponse update(String uuid, MedicineUpdateRequest medicineDTO) {
        var medicine = Optional.ofNullable(medicineDAO.findByUuid(uuid))
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado"));

        Optional.ofNullable(medicineDTO.getNome()).ifPresent(medicine::setNome);
        Optional.ofNullable(medicineDTO.getControlado()).ifPresent(medicine::setControlado);
        Optional.ofNullable(medicineDTO.getPosologia()).ifPresent(medicine::setPosologia);

        medicine = medicineDAO.update(medicine);
        return entityToMedicineDTO(medicine);
    }

    @Transactional
    public void delete(String uuid) {
        var medicine = medicineDAO.findByUuid(uuid);
        if (Objects.isNull(medicine)) {
            throw new IllegalArgumentException("Medicamento não encontrado");
        }

        medicineDAO.delete(medicine.getUuid());
    }

    private MedicineResponse entityToMedicineDTO(Medicine m) {
        return MedicineResponse.builder()
                .idMedicamento(m.getIdMedicamento())
                .uuid(m.getUuid())
                .nome(m.getNome())
                .controlado(m.getControlado())
                .posologia(m.getPosologia())
                .dataCadastro(m.getDataCadastro())
                .build();
    }
}