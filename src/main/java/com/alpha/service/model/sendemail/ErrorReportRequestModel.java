package com.alpha.service.model.sendemail;


import lombok.Data;

/*
 * Created by: fkusu
 * Date: 6/12/2025
 */
@Data
public class ErrorReportRequestModel {
 private String projectName;
 private String errorTime;
 private String user;
 private String url;
 private String userNote;
 private String userMessage;
 private String errorDetail;
}
