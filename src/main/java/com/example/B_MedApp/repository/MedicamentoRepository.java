package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    Optional<Medicamento> findByNombre(String nombre);
    List<Medicamento> findAllByOrderByNombreAsc();
}
