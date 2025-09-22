package com.medical_clinical_app.view;

import com.medical_clinical_app.model.Medicine;
import com.medical_clinical_app.service.MedicineService;
import com.medical_clinical_app.service.PrescriptionService;
import com.medical_clinical_app.service.PrescriptionService.ItemInput;
import com.medical_clinical_app.service.PatientService;
import com.medical_clinical_app.dto.patient.response.PatientResponse;
import com.medical_clinical_app.dto.medicine.response.MedicineResponse;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Named("prescriptionBean")
@ViewScoped
@Data
public class PrescriptionBean implements Serializable {

    @Inject PatientService patientService;
    @Inject MedicineService medicineService;
    @Inject PrescriptionService prescriptionService;

    private String patientUuid;
    private LocalDate prescriptionDate = LocalDate.now();

    private String newMedicineUuid;
    private Integer newQuantity = 1;

    private List<PatientResponse> patients;
    private List<MedicineResponse> medicines;

    public static class ItemRow implements Serializable {
        public String medicineUuid;
        public String medicineName;
        public Integer quantity;
    }
    private List<ItemRow> items = new ArrayList<>();

    @PostConstruct
    public void init() {
        patients = patientService.listAll(0, 200).stream()
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
                .collect(Collectors.toList());

        medicines = medicineService.listAll(0, 200);
    }

    public void addItem() {
        try {
            if (newMedicineUuid == null || newMedicineUuid.isBlank()) {
                error("Selecione um medicamento.");
                return;
            }
            if (newQuantity == null || newQuantity < 1) {
                error("Quantidade inválida.");
                return;
            }
            boolean exists = items.stream().anyMatch(i -> i.medicineUuid.equals(newMedicineUuid));
            if (exists) {
                error("Este medicamento já foi adicionado.");
                return;
            }
            MedicineResponse med = medicines.stream()
                    .filter(m -> m.getUuid().equals(newMedicineUuid))
                    .findFirst().orElse(null);
            if (med == null) {
                error("Medicamento não encontrado.");
                return;
            }
            ItemRow row = new ItemRow();
            row.medicineUuid = med.getUuid();
            row.medicineName = med.getNome();
            row.quantity = newQuantity;
            items.add(row);

            newMedicineUuid = null;
            newQuantity = 1;
            info("Item adicionado.");
        } catch (Exception e) {
            error(msg(e));
        }
    }

    public void removeItem(String medUuid) {
        items.removeIf(i -> Objects.equals(i.medicineUuid, medUuid));
        info("Item removido.");
    }

    public void save() {
        try {
            if (items.isEmpty()) {
                error("Inclua pelo menos um item.");
                return;
            }
            List<ItemInput> payload = items.stream()
                    .map(i -> new ItemInput(i.medicineUuid, i.quantity))
                    .collect(Collectors.toList());

            String rxUuid = prescriptionService.createByUuids(patientUuid, prescriptionDate, payload);

            info("Receita salva! UUID: " + rxUuid);
            items.clear();
            prescriptionDate = LocalDate.now();
            patientUuid = null;
        } catch (Exception e) {
            error(msg(e));
        }
    }

    private void info(String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, m, null)); }
    private void error(String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, m, null)); }
    private String msg(Throwable t) { return (t.getMessage() != null) ? t.getMessage() : t.getClass().getSimpleName(); }

}
