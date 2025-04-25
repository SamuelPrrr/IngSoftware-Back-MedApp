package com.example.B_MedApp.repository;


import com.example.B_MedApp.model.Cita;
import com.example.B_MedApp.model.MedicamentoRecetado;
import com.example.B_MedApp.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    Receta findByIdReceta(Long idReceta);

    List<MedicamentoRecetado> findByIdReceta(long idReceta);

    List<Receta> findAllByCita(Cita Cita);
}
