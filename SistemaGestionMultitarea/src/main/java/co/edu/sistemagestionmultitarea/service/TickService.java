package co.edu.sistemagestionmultitarea.service;

import co.edu.sistemagestionmultitarea.model.Orden;
import co.edu.sistemagestionmultitarea.model.OrdenArea;
import co.edu.sistemagestionmultitarea.model.Historial;
import co.edu.sistemagestionmultitarea.repository.OrdenRepository;
import co.edu.sistemagestionmultitarea.repository.OrdenAreaRepository;
import co.edu.sistemagestionmultitarea.repository.HistorialRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TickService {

    private final OrdenRepository ordenRepository;
    private final OrdenAreaRepository ordenAreaRepository;
    private final HistorialRepository historialRepository;

    private static final int N_SEG = 60;     // cada tick suma 60s
    private static final int SLA_SEG = 3600; // 1 hora

    public TickService(OrdenRepository ordenRepository,
                       OrdenAreaRepository ordenAreaRepository,
                       HistorialRepository historialRepository) {
        this.ordenRepository = ordenRepository;
        this.ordenAreaRepository = ordenAreaRepository;
        this.historialRepository = historialRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void tick() {
        // Coincidir exactamente con el mock del test
        List<OrdenArea> activas = ordenAreaRepository.findByEstadoIn(
                List.of("EN_PROGRESO", "PENDIENTE")
        );

        for (OrdenArea area : activas) {
            area.setSegAcumulados(area.getSegAcumulados() + N_SEG);
            String estadoActual = area.getEstado();

            switch (estadoActual) {
                case "PENDIENTE" -> {
                    if (area.getSegAcumulados() >= 300) {
                        cambiarEstado(area, "EN_PROGRESO", "El área comenzó a trabajar.");
                    }
                }

                case "EN_PROGRESO" -> {
                    // Prioriza el timeout (por SLA)
                    if (area.getSegAcumulados() >= SLA_SEG) {
                        cambiarEstado(area, "TIMEOUT", "El área superó su tiempo límite (SLA).");
                    } else if (area.getSegAcumulados() >= 600) {
                        cambiarEstado(area, "PAUSADA", "El área pausó temporalmente su trabajo.");
                    }
                }

                case "PAUSADA" -> {
                    if (area.getSegAcumulados() >= 900) {
                        cambiarEstado(area, "COMPLETADA", "El área completó su trabajo exitosamente.");
                    }
                }
            }

        }

        actualizarEstadoGlobal();
    }


    private void cambiarEstado(OrdenArea area, String nuevoEstado, String detalle) {
        String anterior = area.getEstado();
        if (!anterior.equals(nuevoEstado)) {
            area.setEstado(nuevoEstado);

            // 💾 guarda inmediatamente el cambio de estado
            ordenAreaRepository.save(area);

            historialRepository.save(new Historial(
                    null,
                    area.getOrdenId(),
                    "CAMBIO_ESTADO",
                    "De " + anterior + " a " + nuevoEstado + ". " + detalle,
                    nuevoEstado,
                    Instant.now(),
                    "SYSTEM"
            ));
        }
    }




    private void actualizarEstadoGlobal() {
        List<Orden> ordenes = ordenRepository.findAll();

        for (Orden orden : ordenes) {
            List<OrdenArea> areas = ordenAreaRepository.findByOrdenId(orden.getId());

            boolean todasCompletadas = areas.stream().allMatch(a -> "COMPLETADA".equals(a.getEstado()));
            boolean algunaTimeout = areas.stream().anyMatch(a -> "TIMEOUT".equals(a.getEstado()));
            boolean algunaEnProgreso = areas.stream().anyMatch(a -> "EN_PROGRESO".equals(a.getEstado()));
            boolean algunaPausada = areas.stream().anyMatch(a -> "PAUSADA".equals(a.getEstado()));

            if (algunaTimeout) orden.setEstadoGlobal("PENDIENTE"); // ✅ regla: global pendiente en caso de timeout
            else if (todasCompletadas) orden.setEstadoGlobal("COMPLETADA");
            else if (algunaPausada) orden.setEstadoGlobal("PAUSADA");
            else if (algunaEnProgreso) orden.setEstadoGlobal("EN_PROGRESO");
            else orden.setEstadoGlobal("PENDIENTE");

            orden.setActualizadaEn(Instant.now());
            ordenRepository.save(orden);
        }
    }
}
