package com.example.B_MedApp.service;

import com.example.B_MedApp.jwt.JwtService;
import com.example.B_MedApp.model.*;
import com.example.B_MedApp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final CitaRepository citaRepository;
    private final MedicamentoRecetadoRepository medicamentoRecetadoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final JwtService jwtService;

    public RecetaService(RecetaRepository recetaRepository, CitaRepository citaRepository, MedicamentoRecetadoRepository medicamentoRecetadoRepository, MedicamentoRepository medicamentoRepository, JwtService jwtService) {
        this.recetaRepository = recetaRepository;
        this.citaRepository = citaRepository;
        this.medicamentoRecetadoRepository = medicamentoRecetadoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.jwtService = jwtService;
    }

    // 1. Crear receta
    public ResponseEntity<Object> crearReceta(Long idCita, String anotaciones) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Cita> citaOpt = citaRepository.findById(idCita);
            if (citaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Cita no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Receta nuevaReceta = new Receta();
            nuevaReceta.setCita(citaOpt.get());
            nuevaReceta.setAnotaciones(anotaciones);
            // La lista de medicamentos se inicializa automáticamente como vacía

            Receta saved = recetaRepository.save(nuevaReceta);

            response.put("error", false);
            response.put("message", "Receta creada exitosamente");
            response.put("data", Map.of(
                    "idReceta", saved.getIdReceta(),
                    "idCita", saved.getCita().getIdCita(),
                    "anotaciones", saved.getAnotaciones(),
                    "cantidadMedicamentos", saved.getMedicamentos().size() // Será 0
            ));

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al crear receta: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> agregarMedicamentoAReceta(
            Long idReceta,
            Long idMedicamento,
            LocalTime frecuencia,
            Integer numeroDias,
            String cantidadDosis) {

        HashMap<String, Object> response = new HashMap<>();
        try {
            // 1. Verificar que la receta existe
            Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
            if (recetaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Receta no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. Verificar que el medicamento existe
            Optional<Medicamento> medicamentoOpt = medicamentoRepository.findById(idMedicamento);
            if (medicamentoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 3. Validar que el medicamento no esté ya en la receta
            boolean medicamentoYaExiste = medicamentoRecetadoRepository.existsByRecetaIdRecetaAndMedicamentoIdMedicamento(
                    idReceta,
                    idMedicamento
            );

            if (medicamentoYaExiste) {
                response.put("error", true);
                response.put("message", "Este medicamento ya está incluido en la receta");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            // 4. Validar frecuencia (evitar división por cero)
            if (frecuencia.getHour() == 0) {
                response.put("error", true);
                response.put("message", "La frecuencia no puede ser cero horas");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // 5. Crear el medicamento recetado
            MedicamentoRecetado nuevoMedicamentoRecetado = new MedicamentoRecetado();
            nuevoMedicamentoRecetado.setReceta(recetaOpt.get());
            nuevoMedicamentoRecetado.setMedicamento(medicamentoOpt.get());
            nuevoMedicamentoRecetado.setFrecuencia(frecuencia);
            nuevoMedicamentoRecetado.setNumeroDias(numeroDias);
            nuevoMedicamentoRecetado.setCantidadDosis(cantidadDosis);

            // Calcular número de dosis automáticamente
            int numDosis = (numeroDias * 24) / frecuencia.getHour();
            nuevoMedicamentoRecetado.setNumDosis(numDosis);
            nuevoMedicamentoRecetado.setDosisActual(0); // Inicializar en 0

            // 6. Guardar el medicamento recetado
            MedicamentoRecetado saved = medicamentoRecetadoRepository.save(nuevoMedicamentoRecetado);

            // 7. Actualizar la lista de medicamentos en la receta
            Receta receta = recetaOpt.get();
            receta.getMedicamentos().add(saved);
            recetaRepository.save(receta);

            // 8. Preparar respuesta
            response.put("error", false);
            response.put("message", "Medicamento añadido a receta exitosamente");
            response.put("data", Map.of(
                    "idMedicamentoRecetado", saved.getIdMedicamentoEnReceta(),
                    "medicamento", saved.getMedicamento().getNombre(),
                    "cantidadDosis", saved.getCantidadDosis(),
                    "frecuencia", saved.getFrecuencia(),
                    "dosisTotal", saved.getNumDosis()
            ));

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al añadir medicamento a receta: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> eliminarMedicamentoDeReceta(Long idReceta, Long idMedicamentoRecetado) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // 1. Verificar que la receta existe
            Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
            if (recetaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Receta no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 2. Verificar que el medicamento recetado existe y pertenece a la receta
            Optional<MedicamentoRecetado> medicamentoRecetadoOpt = medicamentoRecetadoRepository.findById(idMedicamentoRecetado);

            if(medicamentoRecetadoOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Medicamento no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            MedicamentoRecetado medicamento = medicamentoRecetadoOpt.get();

            if(medicamento.getReceta().getIdReceta() != idReceta) {
                response.put("error", true);
                response.put("message", "Medicamento no encontrado en esta receta");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // 3. Eliminar el medicamento recetado
            medicamentoRecetadoRepository.deleteById(idMedicamentoRecetado);

            // 4. Actualizar la lista en memoria (opcional pero recomendado)
            Receta receta = recetaOpt.get();
            receta.getMedicamentos().removeIf(m -> m.getIdMedicamentoEnReceta().equals(idMedicamentoRecetado));

            // 5. Preparar respuesta
            response.put("error", false);
            response.put("message", "Medicamento eliminado de la receta exitosamente");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar medicamento de la receta: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 2. Obtener receta por ID (con medicamentos)
    public ResponseEntity<Object> obtenerReceta(Long idReceta) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
            if (recetaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Receta no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Receta receta = recetaOpt.get();
            Map<String, Object> recetaMap = new LinkedHashMap<>();
            recetaMap.put("idReceta", receta.getIdReceta());
            recetaMap.put("anotaciones", receta.getAnotaciones());

            // Manejo seguro de la cita
            recetaMap.put("cita", Optional.ofNullable(receta.getCita())
                    .map(c -> Map.of(
                            "idCita", c.getIdCita(),
                            "fechaHora", c.getFechaHora(),
                            "motivo", c.getMotivo()
                    ))
                    .orElse(null));

            // Manejo seguro de medicamentos (null-safe)
            List<Map<String, Object>> medicamentosResponse = Optional.ofNullable(receta.getMedicamentos())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(Objects::nonNull) // Filtra elementos null
                    .map(this::mapearMedicamentoRecetado)
                    .collect(Collectors.toList());

            recetaMap.put("medicamentos", medicamentosResponse);

            response.put("error", false);
            response.put("data", recetaMap);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener receta: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> mapearMedicamentoRecetado(MedicamentoRecetado med) {
        Map<String, Object> medMap = new LinkedHashMap<>();
        medMap.put("idMedicamentoRecetado", med.getIdMedicamentoEnReceta());

        // Manejo seguro del medicamento
        medMap.put("medicamento", Optional.ofNullable(med.getMedicamento())
                .map(m -> Map.of(
                        "idMedicamento", m.getIdMedicamento(),
                        "nombre", m.getNombre(),
                        "presentacion", m.getPresentacion()
                ))
                .orElse(null));

        medMap.put("frecuencia", med.getFrecuencia());
        medMap.put("numeroDias", med.getNumeroDias());
        medMap.put("cantidadDosis", med.getCantidadDosis());
        medMap.put("dosisActual", med.getDosisActual());
        medMap.put("numDosis", med.getNumDosis());

        return medMap;
    }

    // 3. Eliminar receta (y sus medicamentos recetados por cascade)
    public ResponseEntity<Object> eliminarReceta(Long idReceta) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Receta> recetaOpt = recetaRepository.findById(idReceta);
            if (recetaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Receta no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            recetaRepository.deleteById(idReceta);

            response.put("error", false);
            response.put("message", "Receta eliminada exitosamente");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al eliminar receta: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 5. Obtener recetas por cita
    public ResponseEntity<Object> obtenerRecetasPorCita(Long idCita) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            Optional<Cita> citaOpt = citaRepository.findById(idCita);

            if (citaOpt.isEmpty()) {
                response.put("error", true);
                response.put("message", "Cita no encontrada");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            List<Receta> recetas = recetaRepository.findAllByCita(citaOpt.get());

            List<Map<String, Object>> recetasResponse = recetas.stream()
                    .map(receta -> {
                        Map<String, Object> recetaMap = new LinkedHashMap<>();
                        recetaMap.put("id", receta.getIdReceta());
                        recetaMap.put("anotaciones", receta.getAnotaciones());
                        recetaMap.put("cantidadMedicamentos", receta.getMedicamentos().size());
                        return recetaMap;
                    })
                    .collect(Collectors.toList());

            response.put("error", false);
            response.put("data", recetasResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Error al obtener recetas: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}