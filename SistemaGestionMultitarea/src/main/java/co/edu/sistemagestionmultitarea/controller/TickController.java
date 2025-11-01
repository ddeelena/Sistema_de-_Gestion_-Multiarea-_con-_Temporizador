package co.edu.sistemagestionmultitarea.controller;

import co.edu.sistemagestionmultitarea.service.TickService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tick")
public class TickController {
    private final TickService tickService;
    public TickController(TickService tickService) { this.tickService = tickService; }

    @PostMapping
    public ResponseEntity<String> runTick() {
        tickService.tick();
        return ResponseEntity.ok("Tick ejecutado correctamente");
    }
}