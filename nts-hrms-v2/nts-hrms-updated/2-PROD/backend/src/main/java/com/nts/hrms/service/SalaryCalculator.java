package com.nts.hrms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Splits annual CTC the way Indian companies typically do:
 *   Basic = x% of CTC, HRA = y% of basic, employer PF (12%, capped at wage ceiling), gratuity (4.81% of basic),
 *   and Special Allowance = the balancing figure. Deductions: employee PF, state professional tax, and TDS
 *   under the new tax regime (FY 2026-27 slabs, Rs 75,000 standard deduction, 87A rebate, 4% cess).
 * Rules change often; have your CA verify before relying on this for real salaries.
 */
@Component
public class SalaryCalculator {
    public record Breakup(double annualCtc, double basic, double hra, double special, double gross,
                          double employerPf, double gratuity, double insurance,
                          double employeePf, double pt, double tds, double net) {}

    private static final double PF_WAGE_CEILING = 15000, PF_RATE = 0.12, GRATUITY_RATE = 0.0481, STD_DEDUCTION = 75000;
    private final double basicPct, hraPct, insuranceAnnual; private final boolean pfCap, gratuityOn;

    public SalaryCalculator(@Value("${payroll.basic-percent:50}") double basicPct,
                            @Value("${payroll.hra-percent-of-basic:40}") double hraPct,
                            @Value("${payroll.pf-cap-enabled:true}") boolean pfCap,
                            @Value("${payroll.gratuity-enabled:true}") boolean gratuityOn,
                            @Value("${payroll.insurance-annual:0}") double insuranceAnnual) {
        this.basicPct = basicPct; this.hraPct = hraPct; this.pfCap = pfCap;
        this.gratuityOn = gratuityOn; this.insuranceAnnual = insuranceAnnual;
    }

    /** Full-month breakup for a given annual CTC. */
    public Breakup compute(double ctc, String branch, int month) {
        if (ctc <= 0) throw new IllegalArgumentException("CTC must be greater than zero");
        double basic = Math.round(ctc * basicPct / 100 / 12);
        double hra = Math.round(basic * hraPct / 100);
        double erPf = Math.round(employeePf(basic));
        double grat = gratuityOn ? Math.round(basic * GRATUITY_RATE) : 0;
        double ins = Math.round(insuranceAnnual / 12);
        double special = Math.round(ctc / 12) - basic - hra - erPf - grat - ins;
        if (special < 0) throw new IllegalArgumentException("CTC is too low for this salary structure");
        double gross = basic + hra + special;
        double pf = erPf, pt = pt(branch, gross, month), tds = Math.round(annualTax(gross * 12) / 12);
        return new Breakup(ctc, basic, hra, special, gross, erPf, grat, ins, pf, pt, tds, gross - pf - pt - tds);
    }

    /** PF on (possibly loss-of-pay-reduced) basic. */
    public double employeePf(double basicMonthly) {
        return PF_RATE * (pfCap ? Math.min(basicMonthly, PF_WAGE_CEILING) : basicMonthly);
    }

    /** State professional tax. Tirupati and Nellore use Andhra Pradesh slabs, Bengaluru uses Karnataka. */
    public double pt(String branch, double gross, int month) {
        if ("Bengaluru".equalsIgnoreCase(branch)) return gross >= 25000 ? (month == 2 ? 300 : 200) : 0;
        return gross > 20000 ? 200 : gross > 15000 ? 150 : 0;
    }

    /** Annual income tax, new regime, FY 2026-27, for a salaried resident individual. */
    public double annualTax(double annualGross) {
        double taxable = Math.max(0, annualGross - STD_DEDUCTION);
        double[] limit = {400000, 800000, 1200000, 1600000, 2000000, 2400000};
        double[] rate = {0, .05, .10, .15, .20, .25, .30};
        double tax = 0, prev = 0; boolean done = false;
        for (int i = 0; i < limit.length && !done; i++) {
            if (taxable > limit[i]) { tax += (limit[i] - prev) * rate[i]; prev = limit[i]; }
            else { tax += (taxable - prev) * rate[i]; done = true; }
        }
        if (!done) tax += (taxable - prev) * rate[6];
        tax = taxable <= 1200000 ? 0 : Math.min(tax, taxable - 1200000);   // 87A rebate + marginal relief
        return tax * 1.04;                                                   // 4% health & education cess
    }
}
