package com.nts.hrms.repository;

import com.nts.hrms.model.Payslip;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PayslipRepo extends ReactiveCrudRepository<Payslip, Long> {
    Flux<Payslip> findByUserCodeOrderByPayMonthDesc(String userCode);
    Flux<Payslip> findByPayMonth(String payMonth);
    Mono<Payslip> findByUserCodeAndPayMonth(String userCode, String payMonth);
}
