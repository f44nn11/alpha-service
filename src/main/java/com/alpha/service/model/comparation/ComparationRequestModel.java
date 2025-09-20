package com.alpha.service.model.comparation;


import com.alpha.service.model.placing.PlacingRequestModel;
import lombok.Data;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 4/26/2025
 */
@Data
public class ComparationRequestModel {
    private String comparationCd;
    private String placingCd;
    private String bookCd;
    private String actionType;
    private String remark;
    private String createBy;
    private String emailRemark;
    private String description;
    private String revDoc;
    private List<InsuranceModel> insurances;
    private List<ComparationModel> comparations;

    // Emergency Force (optional)
    private Boolean force;                 // optional
    private String forceMode;              // "PLACEHOLDER" | "FALLBACK"
    private String forceReason;            // required if force==true
    private List<String> forcedInsurers;   // list insCd as String

    @Data
    public static class InsuranceModel {
        private int insCd;
        private String insName;
        private String premium;
        private String commission;
        private String confirmDate;
        private String proposalDate;
        private String description;
        private List<DocTypeModel> docTypes;
    }
    @Data
    public static class DocTypeModel {
        private String code;
        private String revDoc;
        private String descp;
        private String urlPath;
        private String fileName;
    }
    @Data
    public static class ComparationModel {
        private String comparationCd;
        private String description;
        private String emailRemark;
        private String fileName;
        private String urlPath;
        private String sendDate;
        private List<String> insurances;
        private String rev;
        private String revDoc;
        private String isLatest;
    }
}
