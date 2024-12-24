package com.alpha.booking_account.model.procedure;


import com.alpha.booking_account.model.BookingAccountModel;
import lombok.Data;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */
@Data
public class UspBookingAccountDtlParam {
    private String bookCd;
    private String revDoc;
    private String description;
    private String docType;
    private String pathDoc;
    private String createdBy;
    private String actionType;
}
