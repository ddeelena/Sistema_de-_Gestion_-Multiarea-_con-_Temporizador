package co.edu.sistemagestionmultitarea.controller;


import co.edu.sistemagestionmultitarea.model.Area;
import co.edu.sistemagestionmultitarea.model.Historial;
import co.edu.sistemagestionmultitarea.model.Orden;
import co.edu.sistemagestionmultitarea.model.OrdenArea;
import co.edu.sistemagestionmultitarea.repository.AreaRepository;
import co.edu.sistemagestionmultitarea.repository.HistorialRepository;
import co.edu.sistemagestionmultitarea.repository.OrdenAreaRepository;
import co.edu.sistemagestionmultitarea.repository.OrdenRepository;
import co.edu.sistemagestionmultitarea.request.CrearOrdenRequest;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenRepository ordenRepo;
    private final AreaRepository areaRepo;
    private final OrdenAreaRepository ordenAreaRepo;
    private final HistorialRepository historialRepo;

    public OrdenController(OrdenRepository o, AreaRepository a, OrdenAreaRepository oa, HistorialRepository h) {
        this.ordenRepo = o;
        this.areaRepo = a;
        this.ordenAreaRepo = oa;
        this.historialRepo = h;
    }

    // Crear orden
    @PostMapping
    public ResponseEntity<?> crearOrden(@Valid @RequestBody Orden orden) {
        orden.setCreadaEn(java.time.Instant.now());
        ordenRepo.save(orden);

        historialRepo.save(new Historial(null, orden.getId(), "CREACIÓN", "Orden creada", "NUEVA", java.time.Instant.now(), orden.getCreador()));
        return ResponseEntity.status(HttpStatus.CREATED).body(orden);
    }

    // Listar todas
    @GetMapping
    public List<Orden> listar() {
        return ordenRepo.findAll();
    }

    // Detallar una orden
    @GetMapping("/{id}")
    public ResponseEntity<?> detallar(@PathVariable String id) {
        return ordenRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Orden no encontrada")));
    }

    // Asignar área
    @PostMapping("/{id}/asignar/{areaId}")
    public ResponseEntity<?> asignarArea(@PathVariable String id, @PathVariable String areaId) {
        if (!ordenRepo.existsById(id) || !areaRepo.existsById(areaId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Orden o área inválida"));
        }

        OrdenArea oa = new OrdenArea();
        oa.setOrdenId(id);
        oa.setAreaId(areaId);
        ordenAreaRepo.save(oa);

        historialRepo.save(new Historial(null, id, "ASIGNACIÓN", "Área asignada", "ASIGNADA", java.time.Instant.now(), "sistema"));
        return ResponseEntity.ok(Map.of("mensaje", "Área asignada correctamente"));
    }

    // Cambiar estado parcial
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable String id, @RequestParam String nuevoEstado) {
        Optional<Orden> opt = ordenRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Orden no encontrada"));
        }

        Orden orden = opt.get();
        orden.setEstadoGlobal(nuevoEstado);
        orden.setActualizadaEn(java.time.Instant.now());
        ordenRepo.save(orden);

        historialRepo.save(new Historial(null, id, "CAMBIO_ESTADO", "Cambio a " + nuevoEstado, nuevoEstado, java.time.Instant.now(), "sistema"));
        return ResponseEntity.ok(Map.of("mensaje", "Estado actualizado", "nuevoEstado", nuevoEstado));
    }

    // Historial de orden
    @GetMapping("/{id}/historial")
    public List<Historial> historial(@PathVariable String id) {
        return historialRepo.findByOrdenIdOrderByTimestampAsc(id);
    }

    @PostMapping("/crear")
    public Orden crearOrden(@RequestBody CrearOrdenRequest request) {
        Orden orden = new Orden();
        orden.setTitulo(request.getTitulo());
        orden.setDescripcion(request.getDescripcion());
        orden.setCreador(request.getCreador());
        orden.setEstadoGlobal("NUEVA");
        orden.setCreadaEn(Instant.now());
        orden.setActualizadaEn(Instant.now());
        orden.setAreasAsignadas(request.getAreas());
        orden = ordenRepo.save(orden);

        for (String areaId : request.getAreas()) {
            OrdenArea oa = new OrdenArea();
            oa.setOrdenId(orden.getId());
            oa.setAreaId(areaId);
            oa.setAsignadaA(request.getAsignadaA());
            oa.setEstadoParcial("NUEVA");
            oa.setEstado("PENDIENTE");
            oa.setSegAcumulados(0);
            oa.setSlaSeg(request.getSlaSeg());
            ordenAreaRepo.save(oa);

            historialRepo.save(new Historial(
                    null,
                    orden.getId(),
                    "ASIGNADA",
                    "Orden asignada al área " + areaId,
                    "ASIGNADA",
                    Instant.now(),
                    request.getCreador()
            ));
        }

        actualizarEstadoGlobal(orden.getId());
        return orden;
    }

    private void actualizarEstadoGlobal(String ordenId) {
        Orden orden = ordenRepo.findById(ordenId).orElseThrow();
        List<OrdenArea> areas = ordenAreaRepo.findByOrdenId(ordenId);

        boolean todasCompletadas = areas.stream().allMatch(a -> "COMPLETADA".equals(a.getEstado()));
        boolean algunaTimeout = areas.stream().anyMatch(a -> "TIMEOUT".equals(a.getEstado()));
        boolean algunaEnProgreso = areas.stream().anyMatch(a -> "EN_PROGRESO".equals(a.getEstado()));

        if (algunaTimeout) {
            orden.setEstadoGlobal("TIMEOUT");
        } else if (todasCompletadas) {
            orden.setEstadoGlobal("COMPLETADA");
        } else if (algunaEnProgreso) {
            orden.setEstadoGlobal("EN_PROGRESO");
        } else {
            orden.setEstadoGlobal("PENDIENTE");
        }

        orden.setActualizadaEn(Instant.now());
        ordenRepo.save(orden);
    }

    @GetMapping("/areas")
    public List<Area> obtenerAreas() {
        return areaRepo.findAll();
    }
}
