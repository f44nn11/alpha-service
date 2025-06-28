package com.alpha.service.model.procedure;


import com.alpha.service.model.placing.PlacingRequestModel;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 4/20/2025
 */
@Data
public class UspComparationParam {
    private String comparationCd;
    private String placingCd;
    private String insCd;
    private String p_param1;
    private String p_param2;
    private String p_param3;
    private String actionType;
}
