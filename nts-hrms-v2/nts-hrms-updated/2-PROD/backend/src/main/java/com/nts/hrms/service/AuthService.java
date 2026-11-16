package com.nts.hrms.service;

import com.nts.hrms.model.AppUser;
import com.nts.hrms.model.LoginReq;
import com.nts.hrms.model.PwReq;
import com.nts.hrms.model.UserView;
import com.nts.hrms.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepo users; private final PasswordEncoder enc; private final JwtService jwt;

    public AuthService(UserRepo users, PasswordEncoder enc, JwtService jwt) {
        this.users = users; this.enc = enc; this.jwt = jwt;
    }

    public Mono<Map<String, Object>> login(LoginReq r) {
        return users.findByUserCode(r.userId().trim().toUpperCase())
                .filter(u -> enc.matches(r.password(), u.passwordHash()))
                .map(u -> Map.<String, Object>of("token", jwt.create(u), "user", UserView.of(u)))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid ID or password")));
    }

    public Mono<UserView> me(String userCode) { return users.findByUserCode(userCode).map(UserView::of); }

    public Mono<Map<String, String>> changePassword(String userCode, PwReq r) {
        if (r.newPassword() == null || r.newPassword().length() < 6)
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be at least 6 characters"));
        return users.findByUserCode(userCode)
                .filter(u -> enc.matches(r.oldPassword(), u.passwordHash()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect")))
                .flatMap(u -> users.save(new AppUser(u.id(), u.userCode(), u.name(), u.role(), enc.encode(r.newPassword()),
                        u.branch(), u.course(), u.designation(), u.annualCtc(), u.uan())))
                .thenReturn(Map.of("status", "ok"));
    }
}
