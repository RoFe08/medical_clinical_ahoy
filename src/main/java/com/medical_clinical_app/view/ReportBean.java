package com.medical_clinical_app.view;

import com.medical_clinical_app.dto.report.MedicineAggregate;
import com.medical_clinical_app.dto.report.PatientAggregate;
import com.medical_clinical_app.model.Patient;
import com.medical_clinical_app.service.PatientService;
import com.medical_clinical_app.service.ReportService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named("reportBean")
@ViewScoped
public class ReportBean implements Serializable {

    @Inject
    private ReportService reportService;
    @Inject
    private PatientService patientService;

    @Getter private List<MedicineAggregate> topMedicines;
    @Getter private List<PatientAggregate>  topPatients;

    @Getter private List<Patient> patientsForFilter;

    @Getter @Setter
    private Long selectedPatientId;

    @Getter private List<MedicineAggregate> perPatientTotals;
    @Getter private Long perPatientGrandTotal;

    @PostConstruct
    public void init() {
        topMedicines = reportService.top2MedicinesByQuantity();
        topPatients  = reportService.top2PatientsByQuantity();

        patientsForFilter = patientService.listAll(0, 1000);
    }

    public void loadPatientTotals() {
        if (selectedPatientId == null) {
            perPatientTotals = List.of();
            perPatientGrandTotal = 0L;
            return;
        }
        perPatientTotals = reportService.totalsByMedicineForPatient(selectedPatientId);
        perPatientGrandTotal = reportService.totalQuantityForPatient(selectedPatientId);
    }
}
