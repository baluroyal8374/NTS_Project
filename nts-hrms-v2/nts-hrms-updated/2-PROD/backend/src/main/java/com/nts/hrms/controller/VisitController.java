package com.nts.hrms.controller;

import com.nts.hrms.service.VisitService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
public class VisitController {
    private final VisitService visits;
    public VisitController(VisitService visits) { this.visits = visits; }

    /** Public: counts one visit and returns the running total (used by the website footer). */
    @PostMapping("/api/public/visit")
    Mono<Map<String, Long>> visit() { return visits.record().map(m -> Map.of("total", m.get("total"))); }

    @GetMapping("/api/public/visits")
    Mono<Map<String, Long>> total() { return visits.stats().map(m -> Map.of("total", m.get("total"))); }

}
