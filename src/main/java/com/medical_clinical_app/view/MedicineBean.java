package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.MedicineCreateRequest;
import com.medical_clinical_app.dto.MedicineResponse;
import com.medical_clinical_app.service.MedicineService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("medicineBean")
@RequestScoped
public class MedicineBean implements Serializable {

    @Inject
    private MedicineService medicineService;

    private String nome;
    private Boolean controlado;
    private String posologia;

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

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Boolean getControlado() { return controlado; }
    public void setControlado(Boolean controlado) { this.controlado = controlado; }
    public String getPosologia() { return posologia; }
    public void setPosologia(String posologia) { this.posologia = posologia; }
    public List<MedicineResponse> getMedicines() { return medicines; }
}
