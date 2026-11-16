# Changes in this version

1. **Visitor counter** - website footer shows total visitors (one count per browser per day). Shown in the website stats strip (no admin-portal widget). Backend: `VisitService`, `VisitController`, table `site_visits` (created automatically),
   `/api/public/**` opened in `SecurityConfig`. Nginx rate-limits `/api/public/visit` (see `2-PROD/nginx-narayanadritechnology.com.conf`).
2. **Company wording** - top bar, hero text, footer and page title now read: Software development centre in Bengaluru;
   Training & intern placements in Bengaluru, Tirupati and Nellore.
3. **Intern role** - top bar link reads "Student / Intern / Employee Login". Admin can add Interns (IDs `INT001`...). Interns
   get attendance like students. (`UserService`, People tab.)
4. **Professional admin header** - the "Website" link is removed from the portal header.
   **Payslip** redesigned in the Invensis layout (bordered grid, Earnings (A) / Deductions (B), totals, net salary in words,
   "Authorized by", confidentiality footer); prints as a clean A4 PDF with no browser header/footer text.
   Company address/phone for the payslip header: edit the `CO` constant near `function slip` in `login.html`.

## Update an already-running server
    # backend
    cd ~/backend-src && git pull && cd 2-PROD/backend && mvn -q -DskipTests package
    sudo cp target/nts-hrms-1.0.0.jar /opt/nts-hrms/app.jar && sudo systemctl restart nts-hrms
    # website + portal pages
    sudo cp 2-PROD/netlify-site/index.html 2-PROD/netlify-site/login.html /var/www/narayanadritechnology.com/
    # nginx: add the `visit` rate limit from 2-PROD/nginx-narayanadritechnology.com.conf, then sudo nginx -t && sudo systemctl reload nginx

## UAN (added after review)
Employees now have a UAN (12 digits, optional). It is entered on People > Add, listed in the People table and printed on the payslip.
**Existing database - run ONCE before starting the new backend:**

    sudo mariadb nts_hrms -e "ALTER TABLE users ADD COLUMN uan VARCHAR(20) NULL;"
    # set UAN for existing employees (repeat per person):
    sudo mariadb nts_hrms -e "UPDATE users SET uan='123456789012' WHERE user_code='EMP001';"

New installs get the column automatically from `schema.sql`.
