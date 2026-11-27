package com.nts.hrms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;

@Table("users")
public record AppUser(@Id Long id, String userCode, String name, String role, String passwordHash,
                      String branch, String course, String designation, BigDecimal annualCtc, String uan) {}
