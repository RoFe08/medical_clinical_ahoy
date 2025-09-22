package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.patient.request.PatientCreateRequest;
import com.medical_clinical_app.dto.patient.request.PatientUpdateRequest;
import com.medical_clinical_app.dto.patient.response.PatientResponse;
import com.medical_clinical_app.model.enumeration.GenderEnum;
import com.medical_clinical_app.service.PatientService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named("patientBean")
@RequestScoped
@Data
public class PatientBean implements Serializable {

    @Inject
    private PatientService patientService;

    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private GenderEnum sexo;

    private String editUuid;
    private String editNome;
    private String editCpf;
    private LocalDate editDataNascimento;
    private GenderEnum editSexo;

    private List<PatientResponse> patients;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        patients = patientService.listAll(0, 100)
                .stream()
                .map(p -> PatientResponse.builder()
                        .idPaciente(p.getIdPaciente())
                        .uuid(p.getUuid())
                        .nome(p.getNome())
                        .cpf(p.getCpf())
                        .dataNascimento(p.getDataNascimento())
                        .sexo(p.getSexo())
                        .dtInclusao(p.getDtInclusao())
                        .ativo(p.getAtivo())
                        .build())
                .toList();
    }

    public void create() {
        try {
            var dto = PatientCreateRequest.builder()
                    .nome(nome)
                    .cpf(cpf)
                    .dataNascimento(dataNascimento)
                    .sexo(sexo)
                    .build();
            PatientResponse created = patientService.create(dto);
            addInfo("Paciente criado: " + created.getNome());
            clearForm();
            refresh();
        } catch (Exception e) {
            addError("Erro ao criar paciente: " + messageOf(e));
        }
    }

    public void delete(String uuid) {
        try {
            patientService.deleteByUuid(uuid);
            addInfo("Paciente removido.");
            refresh();
        } catch (Exception e) {
            addError("Erro ao remover: " + messageOf(e));
        }
    }

    private void clearForm() {
        nome = null;
        cpf = null;
        dataNascimento = null;
        sexo = null;
    }

    private void addInfo(String m) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }

    private void addError(String m) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }

    private String messageOf(Throwable t) {
        String m = t.getMessage();
        return m != null ? m : t.getClass().getSimpleName();
    }

    public void openEdit(PatientResponse p) {
        this.editUuid           = p.getUuid();
        this.editNome           = p.getNome();
        this.editCpf            = p.getCpf();
        this.editDataNascimento = p.getDataNascimento();
        this.editSexo           = p.getSexo();
    }

    public void update() {
        try {
            PatientUpdateRequest dto = PatientUpdateRequest.builder()
                    .nome(editNome)
                    .cpf(editCpf)
                    .dataNascimento(editDataNascimento)
                    .sexo(editSexo)
                    .build();

            patientService.update(editUuid, dto);
            addInfo("Paciente atualizado.");
            refresh();
        } catch (Exception e) {
            addError("Erro ao atualizar: " + messageOf(e));
        }
    }

}
