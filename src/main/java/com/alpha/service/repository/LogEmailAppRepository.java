package com.alpha.service.repository;


import com.alpha.service.entity.LogEmailApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Created by: fkusu
 * Date: 6/15/2025
 */
@Repository
public interface LogEmailAppRepository extends JpaRepository<LogEmailApp, Long> {
    List<LogEmailApp> findByStatus(String status);
    List<LogEmailApp> findByRefTypeAndRefId(String refType, String refId);
    List<LogEmailApp> findByMailTo(String mailTo);
    List<LogEmailApp> findByRequestDtBetween(LocalDateTime start, LocalDateTime end);
}
