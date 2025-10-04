package com.medical_clinical_app.view;

import com.medical_clinical_app.service.MedicineService;
import com.medical_clinical_app.service.PatientService;
import com.medical_clinical_app.service.PrescriptionService;
import com.medical_clinical_app.service.PrescriptionService.ItemInput;
import com.medical_clinical_app.dto.patient.response.PatientResponse;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Named("prescriptionBean")
@ViewScoped
@Getter
@Setter
public class PrescriptionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject private PatientService patientService;
    @Inject private MedicineService medicineService;
    @Inject private PrescriptionService prescriptionService;

    private String patientUuid;
    private LocalDate prescriptionDate;

    private List<ItemVM> items = new ArrayList<>();

    private String newMedicineUuid;
    private Integer newQuantity;

    private List<PatientResponse> patients;
    private List<MedicineResponse> medicines;

    @PostConstruct
    public void init() {
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
        prescriptionDate = LocalDate.now();
        newQuantity = 1;
    }

    public void addItem() {
        try {
            if (newMedicineUuid == null || newMedicineUuid.isBlank()) {
                addWarn("Selecione um medicamento.");
                return;
            }
            if (newQuantity == null || newQuantity < 1) {
                addWarn("Quantidade inválida.");
                return;
            }
            boolean exists = items.stream().anyMatch(i -> i.getMedicineUuid().equals(newMedicineUuid));
            if (exists) {
                addWarn("Medicamento já adicionado.");
                return;
            }
            MedicineResponse med = medicines.stream()
                    .filter(m -> Objects.equals(m.getUuid(), newMedicineUuid))
                    .findFirst()
                    .orElse(null);
            if (med == null) {
                addError("Medicamento não encontrado.");
                return;
            }

            items.add(new ItemVM(newMedicineUuid, med.getNome(), newQuantity));
            newMedicineUuid = null;
            newQuantity = 1;

            addInfo("Item adicionado.");
        } catch (Exception e) {
            addError(msg(e));
        }
    }

    public void removeItem(String medicineUuid) {
        items.removeIf(i -> Objects.equals(i.getMedicineUuid(), medicineUuid));
        addInfo("Item removido.");
    }

    public void save() {
        try {
            if (patientUuid == null || patientUuid.isBlank()) {
                addWarn("Selecione o paciente."); return;
            }
            if (prescriptionDate == null) {
                addWarn("Informe a data da prescrição."); return;
            }
            if (items.isEmpty()) {
                addWarn("Inclua pelo menos 1 medicamento."); return;
            }
            List<ItemInput> payload = items.stream()
                    .map(i -> new ItemInput(i.getMedicineUuid(), i.getQuantity()))
                    .toList();

            String rxUuid = prescriptionService.createByUuids(patientUuid, prescriptionDate, payload);

            items = new ArrayList<>();
            newMedicineUuid = null;
            newQuantity = 1;

            addInfo("Receita salva. UUID: " + rxUuid);
        } catch (Exception e) {
            addError(msg(e));
        }
    }

    private void addInfo(String m) { FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, m, null)); }
    private void addWarn(String m) { FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_WARN, m, null)); }
    private void addError(String m) { FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null)); }
    private String msg(Throwable t){ return t.getMessage()!=null?t.getMessage():t.getClass().getSimpleName(); }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ItemVM implements Serializable {
        private static final long serialVersionUID = 1L;
        private String medicineUuid;
        private String medicineName;
        private Integer quantity;
    }

}
