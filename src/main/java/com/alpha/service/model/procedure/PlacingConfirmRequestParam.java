package com.alpha.service.model.procedure;


import com.alpha.service.model.placing.DocItem;
import com.alpha.service.model.placing.PlacingRequestModel;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 4/12/2025
 */
@Data
public class PlacingConfirmRequestParam {
    private String placingCd;           // p_placingCd
    private String bookCd;             // p_bookCd
    private String actionType;         // p_actionType ("1" and "2" = insert / update "3" = delete ins )
    private String createBy;           // p_user

    private List<InsuranceItem> insurances;

    private Map<String, MultipartFile> files;

    @Data
    public static class InsuranceItem {
        private Long insCd;                          // p_insCd
        private Date confirmDate;            // p_confirmDt
        private Date proposalDate;                     // p_proposalDt
        private BigDecimal premium;               // p_premiumTot
        private BigDecimal commission;                   // p_compct
        private String description;                  // p_description
        private List<PlacingRequestModel.DocType> docTypes; // p_docListJson
    }
}
