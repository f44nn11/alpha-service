package com.alpha.service.model.placing;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/*
 * Created by: fkusu
 * Date: 1/19/2025
 */
@Data
public class PlacingRequestModel {
    private String placingCd;
    private String bookCd;
    private String placingDate;
    private String createdBy;
    private String actionType;
    private List<Insurance> insurances;

    @Data
    public static class Insurance {
        private int insCd;
        private String insName;
        private List<DocType> docTypes;
    }
    @Data
    public static class DocType {
        private String code;
        private String revDoc;
        private String descp;
        private String urlPath;
    }
}
