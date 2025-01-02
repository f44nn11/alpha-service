package com.alpha.booking_account.model.sendemail;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 12/30/2024
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequestModel {
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private String mailType;
    private String actionType;
    private boolean isHtml;
    private List<MultipartFile> attachments;
}
