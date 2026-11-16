package com.nts.hrms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("payslips")
public record Payslip(@Id Long id, String userCode, String payMonth, int workingDays, int paidDays, int lopDays,
                      BigDecimal basic, BigDecimal hra, BigDecimal specialAllowance, BigDecimal gross,
                      BigDecimal pf, BigDecimal pt, BigDecimal tds, BigDecimal net,
                      BigDecimal annualCtc, LocalDateTime generatedAt) {}
