package com.nts.hrms.service;

import com.nts.hrms.model.Attendance;
import com.nts.hrms.model.MarkReq;
import com.nts.hrms.repository.AttendanceRepo;
import com.nts.hrms.util.AppTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Service
public class AttendanceService {
    private final AttendanceRepo repo;
    public AttendanceService(AttendanceRepo repo) { this.repo = repo; }

    public Mono<Attendance> checkIn(String userCode) {
        LocalDate d = LocalDate.now(AppTime.IST);
        LocalTime t = LocalTime.now(AppTime.IST).truncatedTo(ChronoUnit.SECONDS);
        return repo.findByUserCodeAndAttDate(userCode, d)
                .switchIfEmpty(Mono.fromSupplier(() -> new Attendance(null, userCode, d, "P", null, null)))
                .flatMap(a -> a.checkIn() != null ? Mono.just(a) : repo.save(a.withStatus("P").withIn(t)));
    }

    public Mono<Attendance> checkOut(String userCode) {
        LocalTime t = LocalTime.now(AppTime.IST).truncatedTo(ChronoUnit.SECONDS);
        return repo.findByUserCodeAndAttDate(userCode, LocalDate.now(AppTime.IST))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check in first")))
                .flatMap(a -> repo.save(a.withOut(t)));
    }

    public Flux<Attendance> mine(String userCode, String month) {
        YearMonth ym = YearMonth.parse(month);
        return repo.findByUserCodeAndAttDateBetween(userCode, ym.atDay(1), ym.atEndOfMonth());
    }

    public Flux<Attendance> byDate(LocalDate date) { return repo.findByAttDate(date); }

    /** Admin marks/corrects attendance: P = present, L = leave, A = absent. */
    public Mono<Attendance> mark(MarkReq r) {
        if (!"PLA".contains(r.status()) || r.status().length() != 1)
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be P, L or A"));
        return repo.findByUserCodeAndAttDate(r.userCode(), r.date())
                .switchIfEmpty(Mono.fromSupplier(() -> new Attendance(null, r.userCode(), r.date(), r.status(), null, null)))
                .flatMap(a -> repo.save(a.withStatus(r.status())));
    }
}
