package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.patient.request.PatientCreateDTO;
import com.medical_clinical_app.dto.patient.response.PatientDTO;
import com.medical_clinical_app.model.enumeration.GenderEnum;
import com.medical_clinical_app.service.PatientService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named("patientBean")
@RequestScoped
public class PatientBean implements Serializable {

    @Inject
    private PatientService patientService;

    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private GenderEnum sexo;

    private List<PatientDTO> patients;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        // ajuste sua paginação conforme quiser
        patients = patientService.listAll(0, 100)
                .stream()
                .map(p -> PatientDTO.builder()
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
            var dto = PatientCreateDTO.builder()
                    .nome(nome)
                    .cpf(cpf)
                    .dataNascimento(dataNascimento)
                    .sexo(sexo)
                    .build();
            PatientDTO created = patientService.create(dto);
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

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public GenderEnum getSexo() { return sexo; }
    public void setSexo(GenderEnum sexo) { this.sexo = sexo; }
    public List<PatientDTO> getPatients() { return patients; }
}
