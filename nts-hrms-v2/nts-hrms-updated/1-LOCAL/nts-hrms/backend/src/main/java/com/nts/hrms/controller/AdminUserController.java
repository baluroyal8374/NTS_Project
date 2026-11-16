package com.nts.hrms.controller;

import com.nts.hrms.model.NewUser;
import com.nts.hrms.model.UserView;
import com.nts.hrms.service.SalaryCalculator;
import com.nts.hrms.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@RestController
public class AdminUserController {
    private final UserService users;
    public AdminUserController(UserService users) { this.users = users; }

    @GetMapping("/api/admin/users")
    Flux<UserView> list() { return users.list(); }

    @GetMapping("/api/admin/salary/preview")
    Mono<SalaryCalculator.Breakup> preview(@RequestParam BigDecimal ctc, @RequestParam(defaultValue = "Tirupati") String branch) {
        return users.preview(ctc, branch);
    }

    @PostMapping("/api/admin/users")
    Mono<UserView> add(@RequestBody NewUser n) { return users.add(n); }
}
