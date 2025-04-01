package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.DiaSemana;
import com.example.B_MedApp.model.HorarioLaboral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HorarioLaboralRepository extends JpaRepository<HorarioLaboral, Long> {
    List<HorarioLaboral> findHorarioByMedico_IdUsuario(Long medicoIdUsuario);
    Optional<HorarioLaboral> findByMedico_IdUsuarioAndDiaSemana(Long medicoIdUsuario, DiaSemana diaSemana);
    List<HorarioLaboral> findHorarioLaboralByDiaSemana(DiaSemana diaSemana);
}
