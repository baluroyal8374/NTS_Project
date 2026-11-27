package com.nts.hrms.model;

import java.math.BigDecimal;

/** Safe projection: never exposes the password hash. */
public record UserView(String userId, String name, String role, String branch, String course,
                       String designation, BigDecimal annualCtc, String uan) {
    public static UserView of(AppUser u) {
        return new UserView(u.userCode(), u.name(), u.role(), u.branch(), u.course(), u.designation(), u.annualCtc(), u.uan());
    }
}
