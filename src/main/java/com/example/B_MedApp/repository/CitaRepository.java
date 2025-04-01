package com.example.B_MedApp.repository;

import com.example.B_MedApp.model.Cita;
import com.example.B_MedApp.model.Medico;
import com.example.B_MedApp.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteIdUsuario(Long idUsuario);

    List<Cita> findByMedicoIdUsuario(Long idUsuario);

    boolean existsByMedicoAndFechaHoraBetween(Medico medico, LocalDateTime fechaHoraAfter, LocalDateTime fechaHoraBefore);

    boolean existsByMedicoAndFechaHoraBetweenAndIdCitaNot(Medico medico, LocalDateTime fechaHoraAfter, LocalDateTime fechaHoraBefore, Long idCita);

    List<Cita> findByPacienteAndFechaHoraBetween(Paciente paciente,LocalDateTime fechaHoraAfter, LocalDateTime fechaHoraBefore);

    List<Cita> findByPacienteOrderByFechaHoraDesc(Paciente paciente);



    //findByPacienteOrderByFechaHoraDesc
}
