CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_code VARCHAR(20) NOT NULL UNIQUE, name VARCHAR(100) NOT NULL,
  role VARCHAR(10) NOT NULL, password_hash VARCHAR(100) NOT NULL, branch VARCHAR(30), course VARCHAR(60),
  designation VARCHAR(60), annual_ctc DECIMAL(12,2), uan VARCHAR(20));
CREATE TABLE IF NOT EXISTS attendance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_code VARCHAR(20) NOT NULL, att_date DATE NOT NULL,
  status CHAR(1) NOT NULL, check_in TIME, check_out TIME, UNIQUE KEY uq_att (user_code, att_date));
CREATE TABLE IF NOT EXISTS payslips (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_code VARCHAR(20) NOT NULL, pay_month CHAR(7) NOT NULL,
  working_days INT, paid_days INT, lop_days INT, basic DECIMAL(10,2), hra DECIMAL(10,2), special_allowance DECIMAL(10,2),
  gross DECIMAL(10,2), pf DECIMAL(10,2), pt DECIMAL(10,2), tds DECIMAL(10,2), net DECIMAL(10,2),
  annual_ctc DECIMAL(12,2), generated_at DATETIME, UNIQUE KEY uq_pay (user_code, pay_month));
CREATE TABLE IF NOT EXISTS site_visits (
  visit_date DATE PRIMARY KEY, hits BIGINT NOT NULL DEFAULT 0);
