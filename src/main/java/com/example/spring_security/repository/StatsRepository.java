package com.example.spring_security.repository;

import com.example.spring_security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends JpaRepository<User, Long> {


    @Query(value = """
    WITH months AS (
        SELECT generate_series(1, 12) AS month
    ),

    -- ===== REGISTRATION BASED ON joined_at =====
    registration_month AS (
        SELECT
            m.month,
            COALESCE(r.count, 0) AS registration_count
        FROM months m
        LEFT JOIN (
            SELECT
                EXTRACT(MONTH FROM joined_at)::INT AS month,
                COUNT(*) AS count
            FROM user_info
            WHERE joined_at IS NOT NULL
              AND EXTRACT(YEAR FROM joined_at) = :year
            GROUP BY 1
        ) r ON r.month = m.month
        ORDER BY m.month
    ),

    -- ===== ACTIVE USERS =====
    active_users_month AS (
        SELECT
            m.month,
            COALESCE(a.count, 0) AS active_user_count
        FROM months m
        LEFT JOIN (
            SELECT
                EXTRACT(MONTH FROM online_at)::INT AS month,
                COUNT(DISTINCT user_id) AS count
            FROM record_online_user
            WHERE EXTRACT(YEAR FROM online_at) = :year
            GROUP BY 1
        ) a ON a.month = m.month
        ORDER BY m.month
    )

    SELECT json_build_object(
        'registration', json_build_object(
            'dataByMonth', (SELECT json_agg(registration_count) FROM registration_month),
            'stats', json_build_object(
                'totalRegistration', (SELECT SUM(registration_count) FROM registration_month),
                'avgMonthly', (SELECT ROUND(AVG(registration_count)) FROM registration_month),
                'highestMonth', (
                    SELECT CONCAT(
                        registration_count, ' (',
                        TO_CHAR(TO_DATE(month::text,'MM'),'Mon'), ')'
                    )
                    FROM registration_month
                    ORDER BY registration_count DESC, month DESC
                    LIMIT 1
                ),
                'growthPercentage', (
                    WITH g AS (
                        SELECT
                            (SELECT registration_count
                             FROM registration_month
                             WHERE registration_count > 0
                             ORDER BY month ASC
                             LIMIT 1) AS first_val,
                            (SELECT registration_count
                             FROM registration_month
                             WHERE registration_count > 0
                             ORDER BY month DESC
                             LIMIT 1) AS last_val
                    )
                    SELECT
                        CASE
                            WHEN first_val IS NULL OR first_val = 0 THEN '0%'
                            ELSE CONCAT(
                                ROUND(((last_val - first_val) * 100.0 / first_val)::numeric, 1),
                                '%'
                            )
                        END
                    FROM g
                )
            )
        ),

        'activeUsers', json_build_object(
            'dataByMonth', (SELECT json_agg(active_user_count) FROM active_users_month),
            'stats', json_build_object(
                'avgActivitiesMonthly', (SELECT ROUND(AVG(active_user_count)) FROM active_users_month),
                'highestMonth', (
                    SELECT CONCAT(
                        active_user_count, ' (',
                        TO_CHAR(TO_DATE(month::text,'MM'),'Mon'), ')'
                    )
                    FROM active_users_month
                    ORDER BY active_user_count DESC, month DESC
                    LIMIT 1
                ),
                'activityPercentage', (
                    SELECT CONCAT(
                        ROUND(
                            (SUM(active_user_count)::numeric /
                                NULLIF((SELECT COUNT(*) FROM user_info), 0) * 100
                            ), 1
                        ),
                        '%'
                    )
                    FROM active_users_month
                ),
                'trend', (
                    WITH t AS (
                        SELECT
                            month,
                            active_user_count,
                            LAG(active_user_count) OVER (ORDER BY month) AS prev
                        FROM active_users_month
                    )
                    SELECT CASE
                        WHEN prev IS NULL THEN 'Stable'
                        WHEN active_user_count > prev THEN 'Rising'
                        WHEN active_user_count < prev THEN 'Falling'
                        ELSE 'Stable'
                    END
                    FROM t
                    WHERE prev IS NOT NULL
                    ORDER BY month DESC
                    LIMIT 1
                )
            )
        )
    )::text AS dashboard_stats;
""", nativeQuery = true)
    String getDashboardStats(@Param("year") int year);




}
