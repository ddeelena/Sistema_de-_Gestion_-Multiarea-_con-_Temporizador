package co.edu.sistemagestionmultitarea.service;

import co.edu.sistemagestionmultitarea.model.Historial;
import co.edu.sistemagestionmultitarea.model.Orden;
import co.edu.sistemagestionmultitarea.model.OrdenArea;
import co.edu.sistemagestionmultitarea.repository.HistorialRepository;
import co.edu.sistemagestionmultitarea.repository.OrdenAreaRepository;
import co.edu.sistemagestionmultitarea.repository.OrdenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TickServiceTest {

    private OrdenRepository ordenRepo;
    private OrdenAreaRepository ordenAreaRepo;
    private HistorialRepository historialRepo;
    private TickService tickService;

    @BeforeEach
    void setUp() {
        ordenRepo = mock(OrdenRepository.class);
        ordenAreaRepo = mock(OrdenAreaRepository.class);
        historialRepo = mock(HistorialRepository.class);
        tickService = new TickService(ordenRepo, ordenAreaRepo, historialRepo);
    }

    @Test
    void caso1_dosAreas_enProgresoYCompletada_estadoGlobalDebeSerEnProgreso() {
        Orden orden = new Orden();
        orden.setId("ORD1");

        OrdenArea a1 = new OrdenArea("1", "ORD1", "A1", "user", "COMPLETADA", "COMPLETADA", 0, 3600);
        OrdenArea a2 = new OrdenArea("2", "ORD1", "A2", "user", "EN_PROGRESO", "EN_PROGRESO", 0, 3600);

        when(ordenRepo.findAll()).thenReturn(List.of(orden));
        when(ordenAreaRepo.findByOrdenId("ORD1")).thenReturn(List.of(a1, a2));

        tickService.tick();

        verify(ordenRepo).save(argThat(o ->
                o.getEstadoGlobal().equals("EN_PROGRESO")
        ));
    }

    @Test
    void caso2_timeoutDebeMarcarAreaYGlobalPendiente() {
        Orden orden = new Orden();
        orden.setId("ORD2");

        OrdenArea a1 = new OrdenArea("1", "ORD2", "A1", "user", "EN_PROGRESO", "EN_PROGRESO", 3600, 3600);
        when(ordenAreaRepo.findByEstadoIn(List.of("EN_PROGRESO", "PENDIENTE")))
                .thenReturn(List.of(a1));
        when(ordenRepo.findAll()).thenReturn(List.of(orden));
        when(ordenAreaRepo.findByOrdenId("ORD2")).thenReturn(List.of(a1));

        tickService.tick();

        verify(ordenAreaRepo).save(argThat(a ->
                a.getEstado().equals("TIMEOUT")
        ));

        verify(ordenRepo).save(argThat(o ->
                o.getEstadoGlobal().equals("PENDIENTE") ||
                        o.getEstadoGlobal().equals("TIMEOUT")
        ));

        verify(historialRepo, atLeastOnce()).save(any(Historial.class));
    }

    @Test
    void caso3_todasCompletadas_estadoGlobalDebeSerCompletada() {
        Orden orden = new Orden();
        orden.setId("ORD3");

        OrdenArea a1 = new OrdenArea("1", "ORD3", "A1", "user", "COMPLETADA", "COMPLETADA", 1000, 3600);
        OrdenArea a2 = new OrdenArea("2", "ORD3", "A2", "user", "COMPLETADA", "COMPLETADA", 2000, 3600);

        when(ordenRepo.findAll()).thenReturn(List.of(orden));
        when(ordenAreaRepo.findByOrdenId("ORD3")).thenReturn(List.of(a1, a2));

        tickService.tick();

        verify(ordenRepo).save(argThat(o ->
                o.getEstadoGlobal().equals("COMPLETADA")
        ));
    }
}