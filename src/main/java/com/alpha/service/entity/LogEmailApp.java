package com.alpha.service.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * Created by: fkusu
 * Date: 6/15/2025
 */
@Entity
@Table(name = "LOG_EMAIL_APP")
@Data
public class LogEmailApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REF_TYPE")
    private String refType;

    @Column(name = "REF_ID")
    private String refId;

    @Column(name = "REF_SUBID")
    private String refSubId;

    @Column(name = "MAIL_TYPE")
    private String mailType;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "MAIL_TO")
    private String mailTo;

    @Column(name = "ATTACHMENT_INFO", columnDefinition = "TEXT")
    private String attachmentInfo;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "STATUS_MESSAGE")
    private String statusMessage;

    @Column(name = "REQUESTDT")
    private LocalDateTime requestDt;

    @Column(name = "SENDDT")
    private LocalDateTime sendDt;

    @Column(name = "FAILEDDT")
    private LocalDateTime failedDt;

    @Column(name = "SERIAL_NUMBER")
    private String serialNumber;

    @Column(name = "CREATEBY")
    private String createBy;

    @Column(name = "UPDATEDT", insertable = false, updatable = false)
    private LocalDateTime updateDt;
}
