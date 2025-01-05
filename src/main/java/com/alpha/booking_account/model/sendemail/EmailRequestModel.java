package com.alpha.booking_account.model.sendemail;


import com.alpha.booking_account.model.BookingAccountModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 12/30/2024
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequestModel extends BookingAccountModel {
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private String mailType;
    private boolean isHtml;
    private List<MultipartFile> attachments;
}
