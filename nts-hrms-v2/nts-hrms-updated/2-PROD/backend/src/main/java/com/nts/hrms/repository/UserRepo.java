package com.nts.hrms.repository;

import com.nts.hrms.model.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveCrudRepository<AppUser, Long> {
    Mono<AppUser> findByUserCode(String userCode);
    Flux<AppUser> findByRole(String role);
}
