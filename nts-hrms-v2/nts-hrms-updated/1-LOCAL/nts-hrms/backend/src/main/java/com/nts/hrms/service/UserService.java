package com.nts.hrms.service;

import com.nts.hrms.model.AppUser;
import com.nts.hrms.model.NewUser;
import com.nts.hrms.model.UserView;
import com.nts.hrms.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

@Service
public class UserService {
    private final UserRepo users; private final PasswordEncoder enc; private final SalaryCalculator calc;

    public UserService(UserRepo users, PasswordEncoder enc, SalaryCalculator calc) {
        this.users = users; this.enc = enc; this.calc = calc;
    }

    public Flux<UserView> list() { return users.findAll().map(UserView::of); }

    /** Live monthly breakup shown while the admin types the CTC. */
    public Mono<SalaryCalculator.Breakup> preview(BigDecimal ctc, String branch) {
        return Mono.fromSupplier(() -> calc.compute(ctc.doubleValue(), branch, 1))
                .onErrorMap(IllegalArgumentException.class, e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    public Mono<UserView> add(NewUser n) {
        String role = n.role() == null ? "" : n.role().toUpperCase();
        if (!role.equals("STUDENT") && !role.equals("EMPLOYEE") && !role.equals("INTERN"))
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be STUDENT, INTERN or EMPLOYEE"));
        if (role.equals("EMPLOYEE")) {
            if (n.annualCtc() == null) return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Annual CTC is required for employees"));
            try { calc.compute(n.annualCtc().doubleValue(), n.branch(), 1); }
            catch (IllegalArgumentException e) { return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())); }
        }
        String uan = n.uan() == null ? "" : n.uan().replaceAll("\\s", "");
        if (!uan.isEmpty() && !uan.matches("\\d{12}"))
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "UAN must be 12 digits"));
        String prefix = role.equals("STUDENT") ? "STU" : role.equals("INTERN") ? "INT" : "EMP";
        return users.findByRole(role).count().flatMap(c -> {
            String pw = n.password() == null || n.password().isBlank() ? "welcome123" : n.password();
            AppUser u = new AppUser(null, prefix + String.format("%03d", c + 1), n.name(), role, enc.encode(pw),
                    n.branch(), n.course(), n.designation(), role.equals("EMPLOYEE") ? n.annualCtc() : null,
                    role.equals("EMPLOYEE") && !uan.isEmpty() ? uan : null);
            return users.save(u).map(UserView::of);
        });
    }
}
