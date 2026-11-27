package com.nts.hrms.service;

import com.nts.hrms.util.AppTime;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.Map;

/** Website visitor counter: one row per day, incremented once per browser per day by the marketing site. */
@Service
public class VisitService {
    private final DatabaseClient db;
    public VisitService(DatabaseClient db) { this.db = db; }

    public Mono<Map<String, Long>> record() {
        return db.sql("INSERT INTO site_visits (visit_date, hits) VALUES (:d, 1) ON DUPLICATE KEY UPDATE hits = hits + 1")
                .bind("d", LocalDate.now(AppTime.IST)).fetch().rowsUpdated().then(stats());
    }

    public Mono<Map<String, Long>> stats() {
        LocalDate today = LocalDate.now(AppTime.IST);
        return db.sql("SELECT CAST(COALESCE(SUM(hits),0) AS SIGNED) AS total, "
                        + "CAST(COALESCE(SUM(CASE WHEN visit_date = :t THEN hits END),0) AS SIGNED) AS today, "
                        + "CAST(COALESCE(SUM(CASE WHEN visit_date >= :w THEN hits END),0) AS SIGNED) AS week FROM site_visits")
                .bind("t", today).bind("w", today.minusDays(6))
                .map((row, meta) -> Map.of("total", row.get("total", Long.class),
                        "today", row.get("today", Long.class), "week", row.get("week", Long.class)))
                .one();
    }
}
