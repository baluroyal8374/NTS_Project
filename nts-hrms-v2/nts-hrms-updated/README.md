# NTS HRMS – updated portal

## What changed
1. **Branded dashboards** – after login every screen uses the same navy/gold look as the login page, with the NTS logo and
   "Narayanadri Technology Solutions" in the header, footer, payslip and as a background watermark.
2. **Backend restructured into layers** – Controller -> Service -> Repository -> Model (see tree below).
   Same API endpoints, same behaviour, same database. Only packages/files were reorganised, and a service class was
   added for auth, users and attendance (controllers are now thin).

**Not changed:** DB username/password, `application.yml`, `schema.sql`, `pom.xml`, `.env.example`, the login page logic.

| Folder | Use it for |
|---|---|
| `1-LOCAL/`  | Run everything on your laptop -> `1-LOCAL/RUN-LOCAL.md` |
| `2-PROD/`   | `netlify-site/` -> Netlify, `backend/` -> your server -> `2-PROD/DEPLOY-PROD.md` |

## Layers (backend/src/main/java/com/nts/hrms)
| Package | Responsibility |
|---|---|
| `controller/` | HTTP endpoints only (`/api/...`), no business logic |
| `service/` | Business rules: login/JWT, users, attendance, payroll, salary calculator |
| `repository/` | Database access (Spring Data R2DBC) |
| `model/` | Entities (`AppUser`, `Attendance`, `Payslip`) and request/response DTOs |
| `config/` | Security/CORS and first-start demo data |
| `util/` | Small shared helpers (IST time zone) |

## Full folder structure
```
nts-hrms-updated/
├── 1-LOCAL/
│   ├── nts-hrms/
│   │   ├── backend/
│   │   │   ├── src/
│   │   │   │   └── main/
│   │   │   │       ├── java/
│   │   │   │       │   └── com/
│   │   │   │       │       └── nts/
│   │   │   │       │           └── hrms/
│   │   │   │       │               ├── config/
│   │   │   │       │               │   ├── SecurityConfig.java
│   │   │   │       │               │   └── Seeder.java
│   │   │   │       │               ├── controller/
│   │   │   │       │               │   ├── AdminUserController.java
│   │   │   │       │               │   ├── AttendanceController.java
│   │   │   │       │               │   ├── AuthController.java
│   │   │   │       │               │   └── PayrollController.java
│   │   │   │       │               ├── model/
│   │   │   │       │               │   ├── AppUser.java
│   │   │   │       │               │   ├── Attendance.java
│   │   │   │       │               │   ├── LoginReq.java
│   │   │   │       │               │   ├── MarkReq.java
│   │   │   │       │               │   ├── NewUser.java
│   │   │   │       │               │   ├── Payslip.java
│   │   │   │       │               │   ├── PwReq.java
│   │   │   │       │               │   └── UserView.java
│   │   │   │       │               ├── repository/
│   │   │   │       │               │   ├── AttendanceRepo.java
│   │   │   │       │               │   ├── PayslipRepo.java
│   │   │   │       │               │   └── UserRepo.java
│   │   │   │       │               ├── service/
│   │   │   │       │               │   ├── AttendanceService.java
│   │   │   │       │               │   ├── AuthService.java
│   │   │   │       │               │   ├── JwtService.java
│   │   │   │       │               │   ├── PayrollService.java
│   │   │   │       │               │   ├── SalaryCalculator.java
│   │   │   │       │               │   └── UserService.java
│   │   │   │       │               ├── util/
│   │   │   │       │               │   └── AppTime.java
│   │   │   │       │               └── HrmsApplication.java
│   │   │   │       └── resources/
│   │   │   │           ├── static/
│   │   │   │           │   ├── index.html
│   │   │   │           │   ├── login-bg.svg
│   │   │   │           │   ├── logo-white.png
│   │   │   │           │   ├── logo.png
│   │   │   │           │   └── nts-pattern.svg
│   │   │   │           ├── application.yml
│   │   │   │           └── schema.sql
│   │   │   ├── .env.example
│   │   │   └── pom.xml
│   │   ├── frontend/
│   │   │   └── public/
│   │   │       ├── index.html
│   │   │       └── login.html
│   │   ├── .gitignore
│   │   └── README.md
│   └── RUN-LOCAL.md
├── 2-PROD/
│   ├── backend/
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/
│   │   │       │   └── com/
│   │   │       │       └── nts/
│   │   │       │           └── hrms/
│   │   │       │               ├── config/
│   │   │       │               │   ├── SecurityConfig.java
│   │   │       │               │   └── Seeder.java
│   │   │       │               ├── controller/
│   │   │       │               │   ├── AdminUserController.java
│   │   │       │               │   ├── AttendanceController.java
│   │   │       │               │   ├── AuthController.java
│   │   │       │               │   └── PayrollController.java
│   │   │       │               ├── model/
│   │   │       │               │   ├── AppUser.java
│   │   │       │               │   ├── Attendance.java
│   │   │       │               │   ├── LoginReq.java
│   │   │       │               │   ├── MarkReq.java
│   │   │       │               │   ├── NewUser.java
│   │   │       │               │   ├── Payslip.java
│   │   │       │               │   ├── PwReq.java
│   │   │       │               │   └── UserView.java
│   │   │       │               ├── repository/
│   │   │       │               │   ├── AttendanceRepo.java
│   │   │       │               │   ├── PayslipRepo.java
│   │   │       │               │   └── UserRepo.java
│   │   │       │               ├── service/
│   │   │       │               │   ├── AttendanceService.java
│   │   │       │               │   ├── AuthService.java
│   │   │       │               │   ├── JwtService.java
│   │   │       │               │   ├── PayrollService.java
│   │   │       │               │   ├── SalaryCalculator.java
│   │   │       │               │   └── UserService.java
│   │   │       │               ├── util/
│   │   │       │               │   └── AppTime.java
│   │   │       │               └── HrmsApplication.java
│   │   │       └── resources/
│   │   │           ├── static/
│   │   │           │   ├── index.html
│   │   │           │   ├── login-bg.svg
│   │   │           │   ├── logo-white.png
│   │   │           │   ├── logo.png
│   │   │           │   └── nts-pattern.svg
│   │   │           ├── application.yml
│   │   │           └── schema.sql
│   │   ├── .env.example
│   │   └── pom.xml
│   ├── netlify-site/
│   │   ├── index.html
│   │   ├── login.html
│   │   └── netlify.toml
│   ├── .gitignore
│   └── DEPLOY-PROD.md
└── README.md
```
