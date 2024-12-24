package com.alpha.booking_account.mapper;


import com.alpha.booking_account.model.BookingAccountModel;
import com.alpha.booking_account.model.procedure.UspBookingAccountDtlParam;
import com.alpha.booking_account.model.procedure.UspBookingAccountParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */
@Mapper
public interface BookingAccountMapper {
    BookingAccountMapper INSTANCE = Mappers.getMapper(BookingAccountMapper.class);
    // Mapping BookingAccountModel ke UspBookingAccountParam
    UspBookingAccountParam toUspBookingAccountParam(BookingAccountModel model);

    // Mapping dari DocType ke UspBookingAccountDtlParam
    @Mapping(target = "bookCd", source = "bookCd") // Set bookCd dari parent
    @Mapping(target = "revDoc", source = "docType.revDoc")
    @Mapping(target = "description", source = "docType.descp")
    @Mapping(target = "docType", source = "docType.code")
    @Mapping(target = "pathDoc", source = "docType.urlPath")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "actionType", source = "actionType")
    UspBookingAccountDtlParam toUspBookingAccountDtlParam(String bookCd, BookingAccountModel.DocType docType,String createdBy,String actionType);
}
