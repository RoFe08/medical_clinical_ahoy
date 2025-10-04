package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.medicine.request.MedicineCreateRequest;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;
import com.medical_clinical_app.dto.medicine.request.MedicineUpdateRequest;
import com.medical_clinical_app.service.MedicineService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named("medicineBean")
@ViewScoped
@Getter
@Setter
public class MedicineBean implements Serializable {

    @Inject
    private MedicineService medicineService;

    private String nome;
    private Boolean controlado;
    private String posologia;

    private String editUuid;
    private String editNome;
    private Boolean editControlado;
    private String editPosologia;

    private List<MedicineResponse> medicines;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        medicines = medicineService.listAll(null, 0, 100).stream().map(medicine ->
                MedicineResponse.builder()
                        .idMedicamento(medicine.getIdMedicamento())
                        .uuid(medicine.getUuid())
                        .nome(medicine.getNome())
                        .controlado(medicine.getControlado())
                        .posologia(medicine.getPosologia())
                        .dataCadastro(medicine.getDataCadastro())
                        .build())
                .toList();
    }

    public void create() {
        try {
            MedicineCreateRequest req = new MedicineCreateRequest();
            req.nome = nome;
            req.controlado = Boolean.TRUE.equals(controlado);
            req.posologia = posologia;

            MedicineResponse created = medicineService.create(req);
            addInfo("Medicamento criado: " + created.nome);
            clear();
            refresh();
        } catch (Exception e) {
            addError("Erro ao criar: " + msg(e));
        }
    }

    public void delete(String uuid) {
        try {
            medicineService.delete(uuid);
            refresh();
        } catch (Exception e) {
            addError("Erro ao remover: " + msg(e));
        }
    }

    public void openEdit(MedicineResponse m) {
        this.editUuid       = m.uuid;
        this.editNome       = m.nome;
        this.editControlado = m.controlado;
        this.editPosologia  = m.posologia;
    }

    public void update() {
        try {
            var dto = MedicineUpdateRequest.builder()
                    .nome(editNome)
                    .controlado(editControlado)
                    .posologia(editPosologia)
                    .build();

            medicineService.update(editUuid, dto);
            addInfo("Medicamento atualizado.");
            refresh();
        } catch (Exception e) {
            addError("Erro ao atualizar: " + msg(e));
        }
    }

    private void clear() {
        nome = null;
        controlado = null;
        posologia = null;
    }

    private void addInfo(String m) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, m, null));
    }
    private void addError(String m) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null));
    }
    private String msg(Throwable t) { return t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName(); }

}
