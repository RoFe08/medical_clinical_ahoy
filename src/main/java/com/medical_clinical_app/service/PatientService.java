package com.medical_clinical_app.service;

import com.medical_clinical_app.dao.PatientDAO;
import com.medical_clinical_app.dto.patient.request.PatientCreateDTO;
import com.medical_clinical_app.dto.patient.request.PatientUpdateDTO;
import com.medical_clinical_app.dto.patient.response.PatientDTO;
import com.medical_clinical_app.model.Medicine;
import com.medical_clinical_app.model.Patient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientService {

    @Inject
    private PatientDAO patientDAO;

    @PersistenceContext(unitName = "appPU")
    EntityManager em;

    @Transactional
    public PatientDTO create(PatientCreateDTO createDTO) {
        Patient patient = createDTOToEntity(createDTO);
        patient = patientDAO.save(patient);
        return entityToPatientDTO(patient);
    }

    public PatientDTO getByUuid(String uuid) {
        Patient p = patientDAO.findByUuid(uuid);
        return entityToPatientDTO(p);
    }

    @Transactional
    public PatientDTO update(String uuid, PatientUpdateDTO updateDTO) {
        Patient patient = Optional.ofNullable(patientDAO.findByUuid(uuid))
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        Optional.ofNullable(updateDTO.getNome()).ifPresent(patient::setNome);
        Optional.ofNullable(updateDTO.getCpf()).ifPresent(patient::setCpf);
        Optional.ofNullable(updateDTO.getDataNascimento()).ifPresent(patient::setDataNascimento);
        Optional.ofNullable(updateDTO.getSexo()).ifPresent(patient::setSexo);

        patient = patientDAO.update(patient);
        return entityToPatientDTO(patient);
    }

    @Transactional
    public void delete(String uuid) {
        Patient patient = Optional.ofNullable(patientDAO.findByUuid(uuid))
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));

        patientDAO.delete(patient);
    }

    @Transactional
    public boolean deleteByUuid(String uuid) {
        List<Patient> list = em.createQuery(
                        "SELECT m FROM Medicine m WHERE m.uuid = :u", Patient.class)
                .setParameter("u", uuid)
                .setMaxResults(1)
                .getResultList();
        if (list.isEmpty()) return false;
        em.remove(list.get(0));
        return true;
    }

    public List<Patient> listAll(int page, int size) {
        return patientDAO.findAll(page, size);
    }

    public List<Patient> search(String search, int page, int size) {
        return patientDAO.searchByNameOrCpf(search, page, size);
    }

    private Patient createDTOToEntity(PatientCreateDTO dto) {
        return Patient.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .dataNascimento(dto.getDataNascimento())
                .sexo(dto.getSexo())
                .build();
    }

    private PatientDTO entityToPatientDTO(Patient entity) {
        return PatientDTO.builder()
                .idPaciente(entity.getIdPaciente())
                .uuid(entity.getUuid())
                .nome(entity.getNome())
                .cpf(entity.getCpf())
                .dataNascimento(entity.getDataNascimento())
                .sexo(entity.getSexo())
                .dtInclusao(entity.getDtInclusao())
                .ativo(entity.getAtivo())
                .build();
    }
}
