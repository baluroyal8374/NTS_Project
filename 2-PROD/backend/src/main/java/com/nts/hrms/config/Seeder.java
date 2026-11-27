package com.nts.hrms.config;

import com.nts.hrms.repository.UserRepo;

import com.nts.hrms.model.AppUser;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/** Creates demo accounts on first start. Change or delete these before going live. */
@Component
public class Seeder implements ApplicationRunner {
    private final UserRepo users; private final PasswordEncoder enc;
    public Seeder(UserRepo users, PasswordEncoder enc) { this.users = users; this.enc = enc; }

    @Override
    public void run(ApplicationArguments args) {
        users.count().filter(c -> c == 0).flatMapMany(c -> users.saveAll(List.of(
            new AppUser(null, "ADMIN", "HR Admin", "ADMIN", enc.encode("admin123"), "Tirupati", null, null, null, null),
            emp("NTS001", "Ravi Kumar", "Tirupati", "Trainer – Java", 1500000),
            emp("NTS002", "Sneha Reddy", "Bengaluru", "Trainer – Python", 960000),
            new AppUser(null, "STU001", "Anitha M", "STUDENT", enc.encode("stu123"), "Tirupati", "Java Full Stack", null, null, null),
            new AppUser(null, "STU002", "Karthik B", "STUDENT", enc.encode("stu123"), "Bengaluru", "MERN Stack", null, null, null)
        ))).blockLast();
    }

    private AppUser emp(String code, String name, String branch, String role, long ctc) {
        return new AppUser(null, code, name, "EMPLOYEE", enc.encode("emp123"), branch, null, role, BigDecimal.valueOf(ctc), null);
    }
}
