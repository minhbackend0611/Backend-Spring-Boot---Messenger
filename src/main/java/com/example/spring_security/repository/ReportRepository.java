package com.example.spring_security.repository;

import com.example.spring_security.entities.Report;
import com.example.spring_security.entities.ReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, ReportId> {
    @Query(value = """
    SELECT r.* 
    FROM report r
    JOIN user_info u ON u.user_id = r.reporter_id
    WHERE
        (:username IS NULL OR u.username LIKE %:username%)
    AND (:email IS NULL OR u.email LIKE %:email%)
    ORDER BY 
        CASE WHEN :sortBy = 'reportedAt' THEN r.reported_at END ASC,
        CASE WHEN :sortBy = '-reportedAt' THEN r.reported_at END DESC,
        CASE WHEN :sortBy = 'username' THEN u.username END ASC,
        CASE WHEN :sortBy = '-username' THEN u.username END DESC
""", nativeQuery = true)
    List<Report> findReportsWithFilterAndOrderBy(
            @Param("sortBy") String sortBy,
            @Param("username") String username,
            @Param("email") String email
    );

    Optional<Report> findById(ReportId id);

}
