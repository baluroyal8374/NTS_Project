package com.nts.hrms.controller;

import com.nts.hrms.model.Attendance;
import com.nts.hrms.model.MarkReq;
import com.nts.hrms.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.security.Principal;
import java.time.LocalDate;

@RestController
public class AttendanceController {
    private final AttendanceService svc;
    public AttendanceController(AttendanceService svc) { this.svc = svc; }

    @PostMapping("/api/attendance/check-in")
    Mono<Attendance> checkIn(Principal p) { return svc.checkIn(p.getName()); }

    @PostMapping("/api/attendance/check-out")
    Mono<Attendance> checkOut(Principal p) { return svc.checkOut(p.getName()); }

    @GetMapping("/api/attendance/me")
    Flux<Attendance> mine(Principal p, @RequestParam String month) { return svc.mine(p.getName(), month); }

    @GetMapping("/api/admin/attendance")
    Flux<Attendance> byDate(@RequestParam LocalDate date) { return svc.byDate(date); }

    @PutMapping("/api/admin/attendance")
    Mono<Attendance> mark(@RequestBody MarkReq r) { return svc.mark(r); }
}
