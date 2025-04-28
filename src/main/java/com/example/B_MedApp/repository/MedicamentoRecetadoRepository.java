package com.example.B_MedApp.repository;


import com.example.B_MedApp.model.Medicamento;
import com.example.B_MedApp.model.MedicamentoRecetado;
import com.example.B_MedApp.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRecetadoRepository extends JpaRepository<MedicamentoRecetado, Long> {

    @Query("SELECT mr FROM MedicamentoRecetado mr " +
            "JOIN mr.receta r " +
            "JOIN r.cita c " +
            "JOIN c.paciente p " +
            "WHERE p.idUsuario = :pacienteId")
    List<MedicamentoRecetado> findAllByPacienteId(@Param("pacienteId") Long pacienteId);

    boolean existsByRecetaAndMedicamento(Receta receta, Medicamento medicamento);

    Optional<MedicamentoRecetado> findByIdMedicamentoEnReceta(Long idMedicamento);

}
