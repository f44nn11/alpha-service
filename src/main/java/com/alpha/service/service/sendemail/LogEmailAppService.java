package com.alpha.service.service.sendemail;


import com.alpha.service.entity.LogEmailApp;
import com.alpha.service.repository.LogEmailAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Created by: fkusu
 * Date: 6/15/2025
 */
@Service
public class LogEmailAppService {
    @Autowired
    private LogEmailAppRepository logEmailAppRepository;

    public LogEmailApp createLog(LogEmailApp log) {
        log.setRequestDt(LocalDateTime.now());
        return logEmailAppRepository.save(log);
    }
    public LogEmailApp updateLog(LogEmailApp log) {
        return logEmailAppRepository.save(log);
    }
    public List<LogEmailApp> getLogsByStatus(String status) {
        return logEmailAppRepository.findByStatus(status);
    }

    public List<LogEmailApp> getLogsByRefTypeAndRefId(String refType, String refId) {
        return logEmailAppRepository.findByRefTypeAndRefId(refType, refId);
    }

    public List<LogEmailApp> getLogsByMailTo(String mailTo) {
        return logEmailAppRepository.findByMailTo(mailTo);
    }

    public List<LogEmailApp> getLogsByRequestDtBetween(LocalDateTime start, LocalDateTime end) {
        return logEmailAppRepository.findByRequestDtBetween(start, end);
    }
    public List<LogEmailApp> getAllLogs() {
        return logEmailAppRepository.findAll();
    }
    public LogEmailApp getLogById(Long id) {
        return logEmailAppRepository.findById(id).orElse(null);
    }
    public void deleteLogById(Long id) {
        logEmailAppRepository.deleteById(id);
    }

}
