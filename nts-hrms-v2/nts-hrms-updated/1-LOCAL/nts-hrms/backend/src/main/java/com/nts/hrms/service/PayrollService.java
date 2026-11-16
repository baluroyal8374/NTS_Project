package com.nts.hrms.service;

import com.nts.hrms.model.AppUser;
import com.nts.hrms.model.Payslip;
import com.nts.hrms.repository.AttendanceRepo;
import com.nts.hrms.repository.PayslipRepo;
import com.nts.hrms.repository.UserRepo;
import com.nts.hrms.util.AppTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

@Service
public class PayrollService {
    private final UserRepo users; private final AttendanceRepo att; private final PayslipRepo slips; private final SalaryCalculator calc;

    public PayrollService(UserRepo users, AttendanceRepo att, PayslipRepo slips, SalaryCalculator calc) {
        this.users = users; this.att = att; this.slips = slips; this.calc = calc;
    }

    /** Runs automatically at 23:55 IST on the last day of every month. */
    @Scheduled(cron = "0 55 23 L * *", zone = "Asia/Kolkata")
    void monthEnd() { run(YearMonth.now(AppTime.IST), false).subscribe(); }

    /** Payslips of one user, newest month first. */
    public Flux<Payslip> mine(String userCode) { return slips.findByUserCodeOrderByPayMonthDesc(userCode); }

    /** All payslips of a month (admin). */
    public Flux<Payslip> byMonth(String month) { return slips.findByPayMonth(month); }

    /** Generates payslips for all employees that have a CTC; returns how many were created/updated. */
    public Mono<Long> run(YearMonth ym, boolean force) {
        return users.findByRole("EMPLOYEE").concatMap(u -> generate(u, ym, force)).count();
    }

    private Mono<Payslip> generate(AppUser u, YearMonth ym, boolean force) {
        if (u.annualCtc() == null) return Mono.empty();
        String m = ym.toString();
        return slips.findByUserCodeAndPayMonth(u.userCode(), m).map(Optional::of).defaultIfEmpty(Optional.empty())
            .flatMap(existing -> {
                if (existing.isPresent() && !force) return Mono.<Payslip>empty();
                return att.findByUserCodeAndAttDateBetween(u.userCode(), ym.atDay(1), ym.atEndOfMonth())
                    .collectList().flatMap(list -> {
                        int wd = workingDays(ym);                                         // Mon–Sat
                        int paid = (int) Math.min(wd, list.stream().filter(a -> !"A".equals(a.status())).count());
                        double f = (double) paid / wd;                                    // absent days = loss of pay
                        var b = calc.compute(u.annualCtc().doubleValue(), u.branch(), ym.getMonthValue());
                        long basic = Math.round(b.basic() * f), hra = Math.round(b.hra() * f), spec = Math.round(b.special() * f);
                        long gross = basic + hra + spec;
                        long pf = Math.round(calc.employeePf(basic));
                        long pt = Math.round(calc.pt(u.branch(), gross, ym.getMonthValue()));
                        long tds = Math.min(Math.round(b.tds()), Math.max(0, gross - pf - pt));
                        return slips.save(new Payslip(existing.map(Payslip::id).orElse(null), u.userCode(), m, wd, paid, wd - paid,
                                bd(basic), bd(hra), bd(spec), bd(gross), bd(pf), bd(pt), bd(tds), bd(gross - pf - pt - tds),
                                u.annualCtc(), LocalDateTime.now(AppTime.IST)));
                    });
            });
    }

    private static BigDecimal bd(long v) { return BigDecimal.valueOf(v); }

    private static int workingDays(YearMonth ym) {
        int c = 0;
        for (int d = 1; d <= ym.lengthOfMonth(); d++) if (ym.atDay(d).getDayOfWeek() != DayOfWeek.SUNDAY) c++;
        return c;
    }
}
