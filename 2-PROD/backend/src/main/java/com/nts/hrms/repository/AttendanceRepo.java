package com.nts.hrms.repository;

import com.nts.hrms.model.Attendance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface AttendanceRepo extends ReactiveCrudRepository<Attendance, Long> {
    Mono<Attendance> findByUserCodeAndAttDate(String userCode, LocalDate date);
    Flux<Attendance> findByUserCodeAndAttDateBetween(String userCode, LocalDate from, LocalDate to);
    Flux<Attendance> findByAttDate(LocalDate date);
}
