package com.medical_clinical_app.service;

import com.medical_clinical_app.dao.PatientDAO;
import com.medical_clinical_app.dto.patient.request.PatientCreateRequest;
import com.medical_clinical_app.dto.patient.request.PatientUpdateRequest;
import com.medical_clinical_app.dto.patient.response.PatientResponse;
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
    public PatientResponse create(PatientCreateRequest createDTO) {
        Patient patient = createDTOToEntity(createDTO);
        patient = patientDAO.save(patient);
        return entityToPatientDTO(patient);
    }

    public PatientResponse getByUuid(String uuid) {
        Patient p = patientDAO.findByUuid(uuid);
        return entityToPatientDTO(p);
    }

    @Transactional
    public PatientResponse update(String uuid, PatientUpdateRequest updateDTO) {
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

    public List<Patient> listAll(int page, int size) {
        return patientDAO.findAll(page, size);
    }

    public List<Patient> search(String search, int page, int size) {
        return patientDAO.searchByNameOrCpf(search, page, size);
    }

    private Patient createDTOToEntity(PatientCreateRequest dto) {
        return Patient.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .dataNascimento(dto.getDataNascimento())
                .sexo(dto.getSexo())
                .build();
    }

    private PatientResponse entityToPatientDTO(Patient entity) {
        return PatientResponse.builder()
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
