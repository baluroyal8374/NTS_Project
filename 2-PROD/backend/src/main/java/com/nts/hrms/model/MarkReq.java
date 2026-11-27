package com.nts.hrms.model;

import java.time.LocalDate;

public record MarkReq(String userCode, LocalDate date, String status) {}
