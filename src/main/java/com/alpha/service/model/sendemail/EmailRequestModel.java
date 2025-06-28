package com.alpha.service.model.sendemail;


import com.alpha.service.model.BookingAccountModel;
import com.alpha.service.model.placing.PlacingInterface;
import com.alpha.service.model.placing.PlacingRequestModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 12/30/2024
 */

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailRequestModel extends BookingAccountModel  {
    private PlacingRequestModel placingRequest;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private Map<String, Object> paramTemplate;
    private String code;
    private String mailType;
    private boolean isHtml;
    private List<MultipartFile> attachments;

}
