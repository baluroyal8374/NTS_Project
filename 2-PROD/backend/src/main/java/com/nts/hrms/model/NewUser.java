package com.nts.hrms.model;

import java.math.BigDecimal;

public record NewUser(String name, String role, String branch, String course, String designation,
                      BigDecimal annualCtc, String password, String uan) {}
