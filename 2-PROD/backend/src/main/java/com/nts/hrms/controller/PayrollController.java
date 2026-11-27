package com.nts.hrms.controller;

import com.nts.hrms.model.Payslip;
import com.nts.hrms.service.PayrollService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.security.Principal;
import java.time.YearMonth;
import java.util.Map;

@RestController
public class PayrollController {
    private final PayrollService payroll;
    public PayrollController(PayrollService payroll) { this.payroll = payroll; }

    @GetMapping("/api/payslips/me")
    Flux<Payslip> mine(Principal p) { return payroll.mine(p.getName()); }

    @GetMapping("/api/admin/payslips")
    Flux<Payslip> byMonth(@RequestParam String month) { return payroll.byMonth(month); }

    @PostMapping("/api/admin/payroll/run")
    Mono<Map<String, Long>> run(@RequestParam String month, @RequestParam(defaultValue = "false") boolean force) {
        return payroll.run(YearMonth.parse(month), force).map(n -> Map.of("generated", n));
    }
}
