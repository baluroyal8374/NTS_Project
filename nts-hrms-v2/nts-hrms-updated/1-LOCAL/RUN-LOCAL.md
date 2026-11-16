# Run locally

1. Install Java 21 and MySQL 8, then in MySQL: `CREATE DATABASE nts_hrms;`
2. Open `nts-hrms/backend` in IntelliJ (Maven project).
3. Run > Edit Configurations > HrmsApplication > Environment variables (use YOUR existing values):
   `DB_USER=root;DB_PASS=<your password>;JWT_SECRET=<32+ random characters>`
4. Run `HrmsApplication` -> API on http://localhost:9091 (tables + demo users are created on first start).

Then open the portal in either way:

**A. Served by the backend (simplest)** – open http://localhost:9091 and sign in.

**B. With the website** – serve `nts-hrms/frontend/public` with VS Code Live Server (http://127.0.0.1:5500),
click **Login**. `login.html` already points to `http://localhost:9091`.
(Do not open the files with `file://`.)

Demo logins: `ADMIN/admin123`, `EMP001/emp123`, `STU001/stu123`.
