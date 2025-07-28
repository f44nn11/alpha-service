package com.alpha.service.model.placing;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/*
 * Created by: fkusu
 * Date: 1/19/2025
 */
@Data
public class PlacingRequestModel implements PlacingInterface {
    private String placingCd;
    private String bookCd;
    private String rev;
    private String placingDate;
    private String description;
    private String createdBy;
    private String actionType;
    private List<Insurance> insurances;


    @Override
    public String getPlacingCd() {
        return placingCd;
    }

    @Override
    public void setPlacingCd(String placingCd) {
        this.placingCd = placingCd;
    }

    @Data
    public static class Insurance {
        private int insCd;
        private String insName;
        private String placingDate;
        private String descriptionDtl;
        private String status;
        private List<DocType> docTypes;
    }
    @Data
    public static class DocType {
        private String code;
        private String revDoc;
        private String descp;
        private String urlPath;
        private boolean isGlobal;
        private boolean isPerInsurance;
    }
}
