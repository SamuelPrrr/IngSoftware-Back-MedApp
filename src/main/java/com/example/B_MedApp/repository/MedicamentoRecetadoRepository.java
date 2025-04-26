package com.example.B_MedApp.repository;


import com.example.B_MedApp.model.Medicamento;
import com.example.B_MedApp.model.MedicamentoRecetado;
import com.example.B_MedApp.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRecetadoRepository extends JpaRepository<MedicamentoRecetado, Long> {

    boolean existsByRecetaIdRecetaAndMedicamentoIdMedicamento(Long recetaId, Long medicamentoId);

    boolean existsByRecetaAndMedicamento(Receta receta, Medicamento medicamento);

    Optional<MedicamentoRecetado> findByIdMedicamentoEnReceta(Long idMedicamento);

}
