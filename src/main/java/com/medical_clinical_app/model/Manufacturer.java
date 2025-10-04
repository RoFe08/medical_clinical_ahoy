//package com.medical_clinical_app.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Table(name="manufacture")
//@Getter
//@Setter
//public class Manufacturer {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_medicamento")
//    private Long idManufacte;
//
//    @NotBlank
//    @Column(nullable = false, length = 150)
//    private String nome;
//
//    @NotBlank
//    @Column(nullable = false, length = 150)
//    private LocalDateTime dtCadastro;
//
//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "medicine_id", nullable = false)
//    private List<Medicine> medicine;
//
//}
