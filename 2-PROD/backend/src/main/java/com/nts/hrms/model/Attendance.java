package com.nts.hrms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Table("attendance")
public record Attendance(@Id Long id, String userCode, LocalDate attDate, String status,
                         LocalTime checkIn, LocalTime checkOut) {
    public Attendance withStatus(String s) { return new Attendance(id, userCode, attDate, s, checkIn, checkOut); }
    public Attendance withIn(LocalTime t)  { return new Attendance(id, userCode, attDate, status, t, checkOut); }
    public Attendance withOut(LocalTime t) { return new Attendance(id, userCode, attDate, status, checkIn, t); }
}
