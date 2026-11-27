-- Rename employee IDs from EMP001, EMP002, ... to NTS001, NTS002, ...
-- Run ONCE on the live database, with the backend STOPPED, then deploy the new backend.
-- 1) Back up first:   sudo mariadb-dump nts_hrms > nts_hrms_backup_before_nts_ids.sql
-- 2) Run:             sudo mariadb nts_hrms < migrate-emp-to-nts.sql
-- Interns (INT...) and students (STU...) are not changed. Passwords and roles are not changed.

START TRANSACTION;

-- Safety check: this should return NO rows (no NTS IDs exist yet). If it returns rows, stop and ask for help.
SELECT user_code FROM users WHERE user_code LIKE 'NTS%';

UPDATE users      SET user_code = CONCAT('NTS', SUBSTRING(user_code, 4)) WHERE user_code LIKE 'EMP%';
UPDATE attendance SET user_code = CONCAT('NTS', SUBSTRING(user_code, 4)) WHERE user_code LIKE 'EMP%';
UPDATE payslips   SET user_code = CONCAT('NTS', SUBSTRING(user_code, 4)) WHERE user_code LIKE 'EMP%';

-- Check the result: every employee should now start with NTS
SELECT user_code, name, role FROM users WHERE role = 'EMPLOYEE' ORDER BY user_code;
SELECT COUNT(*) AS still_emp_in_attendance FROM attendance WHERE user_code LIKE 'EMP%';
SELECT COUNT(*) AS still_emp_in_payslips   FROM payslips   WHERE user_code LIKE 'EMP%';

COMMIT;
