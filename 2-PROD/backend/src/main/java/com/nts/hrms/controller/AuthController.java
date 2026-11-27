package com.nts.hrms.controller;

import com.nts.hrms.model.LoginReq;
import com.nts.hrms.model.PwReq;
import com.nts.hrms.model.UserView;
import com.nts.hrms.service.AuthService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.security.Principal;
import java.util.Map;

@RestController
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/api/auth/login")
    Mono<Map<String, Object>> login(@RequestBody LoginReq r) { return auth.login(r); }

    @GetMapping("/api/me")
    Mono<UserView> me(Principal p) { return auth.me(p.getName()); }

    @PutMapping("/api/me/password")
    Mono<Map<String, String>> changePassword(Principal p, @RequestBody PwReq r) { return auth.changePassword(p.getName(), r); }
}
