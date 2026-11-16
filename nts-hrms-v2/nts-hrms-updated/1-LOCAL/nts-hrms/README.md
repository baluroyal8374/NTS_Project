# NTS HRMS – Narayanadri Technology Solutions

Website + HRMS (students, employees, attendance, payroll and monthly payslips).

## Technology
- Java 21, Spring Boot 3.3.5, Maven
- Spring WebFlux (reactive), Spring Data R2DBC, MySQL 8
- JWT login, roles: ADMIN, EMPLOYEE, STUDENT
- Frontend: plain HTML/JS, hosted on Netlify

## Folder structure
```
backend/                 Spring Boot API  (open this folder in IntelliJ as a Maven project)
  pom.xml
  src/main/java/com/nts/hrms/   controllers, services, security, repositories
  src/main/resources/           application.yml, schema.sql, static/ (portal served by the backend)
frontend/public/
  index.html             public website (has the Login button)
  login.html             login page + student / employee / admin dashboards (calls the API)
```

## How login works
1. Visitor clicks **Login** on `index.html` and opens `login.html`.
2. `login.html` sends `POST {API}/api/auth/login` with the User ID and password.
3. The Spring Boot backend checks the BCrypt password in MySQL and returns a JWT plus the user's role.
4. The page stores the token for the browser session and shows the dashboard for that role.
   Every later call sends `Authorization: Bearer <token>`.
5. Accounts are created only by the Admin (People tab). There is no self sign-up.

## Run locally
1. Install Java 21, MySQL 8.
2. In MySQL: `CREATE DATABASE nts_hrms;`
3. IntelliJ: open `backend/`, wait for Maven to import.
4. Run > Edit Configurations > `HrmsApplication` > Environment variables:
   `DB_USER=root;DB_PASS=yourpassword;JWT_SECRET=<32+ random characters>`
   (see `backend/.env.example` for all options)
5. Run `HrmsApplication`. API: http://localhost:9091 (tables and demo users are created on first start).
6. Serve the website: open `frontend/public` with VS Code Live Server (http://127.0.0.1:5500) or any static server,
   then click **Login**. Do not open the files with `file://`, because browsers block API calls from there.

Demo logins (change before going live): `ADMIN/admin123`, `EMP001/emp123`, `STU001/stu123`.

## Go live
1. Deploy `backend/` on a server with HTTPS (for example `https://hrms.narayanadritech.com`) and set the environment variables there:
   `DB_HOST, DB_USER, DB_PASS, JWT_SECRET, CORS_ORIGINS=https://narayanadri-tech.netlify.app`.
2. In `frontend/public/login.html`, change the `API` constant near the top to that HTTPS address.
3. Deploy `frontend/public/` to Netlify.
4. Delete the demo users and the demo-login hint line in `login.html`.

## Security notes
- Keep secrets out of source control (`.gitignore` already excludes `.env`).
- Use a long random `JWT_SECRET`, and change the default demo passwords.
- Payroll rules (PF, professional tax, TDS) must be confirmed by your CA before real salaries are paid.

## API summary
| Method | Path | Who |
|---|---|---|
| POST | /api/auth/login | public |
| GET | /api/me | any signed-in user |
| PUT | /api/me/password | any signed-in user |
| POST | /api/attendance/check-in, /check-out | any |
| GET | /api/attendance/me?month=YYYY-MM | any |
| GET / PUT | /api/admin/attendance | admin |
| GET / POST | /api/admin/users | admin |
| GET | /api/admin/salary/preview?ctc=&branch= | admin |
| GET | /api/payslips/me | employee |
| GET | /api/admin/payslips?month= | admin |
| POST | /api/admin/payroll/run?month=&force= | admin |

Payroll runs automatically at 23:55 IST on the last day of every month.
