package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.medicine.request.MedicineCreateRequest;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;
import com.medical_clinical_app.service.MedicineService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Named("medicineBean")
@RequestScoped
@Data
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
        medicines = medicineService.listAll(0, 100);
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
            boolean ok = medicineService.deleteByUuid(uuid);
            if (ok) addInfo("Medicamento removido.");
            else    addError("Medicamento não encontrado.");
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
            var dto = new com.medical_clinical_app.dto.medicine.request.MedicineUpdateRequest();
            dto.nome       = editNome;
            dto.controlado = editControlado;
            dto.posologia  = editPosologia;

            medicineService.updateByUuid(editUuid, dto);
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
