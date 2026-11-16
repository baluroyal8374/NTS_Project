# Deploy to production

Netlify hosts only the static website. The Spring Boot API and MySQL must run on a server with HTTPS
(Render, Railway, AWS, a VPS, ...). Order matters: backend first, then the Netlify site.

## 1. Backend (`2-PROD/backend`)
Set these environment variables on the server (use your real DB values – nothing is hard-coded):

    DB_HOST=<mysql host>
    DB_USER=<your db user>
    DB_PASS=<your db password>
    JWT_SECRET=<32+ random characters, NOT the default>
    CORS_ORIGINS=https://narayanadri-tech.netlify.app

Build/run: `mvn clean package` then `java -jar target/nts-hrms-1.0.0.jar` (port from `PORT`, default 9091).
It must be reachable over **HTTPS**, e.g. `https://hrms.narayanadritech.com`.

## 2. Point the site to the backend (already done in this build)
The browser only talks to `/api/...` on your Netlify site. Netlify forwards those calls to the backend, so the backend
address is never shown to visitors, and there are no CORS problems.
The backend address lives in ONE place: `netlify-site/netlify.toml`, on the line `to = "https://.../api/:splat"`.
To move the backend (new Railway address or your own domain), change only that line and redeploy the folder.

## 3. Netlify
Deploy the **`netlify-site`** folder (must include `netlify.toml`, otherwise `/api` will not be forwarded) (Netlify > Sites > narayanadri-tech > Deploys > drag & drop,
or connect the repo with publish directory `netlify-site`). Then open
https://narayanadri-tech.netlify.app/login.html

## Before real use
- The demo-login hint is already hidden in this build, but the demo users (ADMIN/admin123 etc.) still exist in the
  database: sign in as ADMIN, change its password, and remove the demo EMP/STU users.
- Payroll rules (PF, PT, TDS) should be confirmed by your CA before real salaries are paid.
