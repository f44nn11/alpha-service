package com.alpha.service.mapper;


import com.alpha.service.model.BookingAccountModel;
import com.alpha.service.model.procedure.UspBookingAccountDtlParam;
import com.alpha.service.model.procedure.UspBookingAccountParam;
import com.google.gson.Gson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */
@Mapper
public interface BookingAccountMapper {
    BookingAccountMapper INSTANCE = Mappers.getMapper(BookingAccountMapper.class);
    // Mapping BookingAccountModel ke UspBookingAccountParam
    @Mapping(target = "insPlacing", source = "insPlacing", qualifiedByName = "mapInsPlacingToJson")
    @Mapping(target = "clientCode", source = "clientCode.code")
    @Mapping(target = "mktId", source = "mktId.code")
    @Mapping(target = "prevIns", source = "prevIns.code")
    UspBookingAccountParam toUspBookingAccountParam(BookingAccountModel model);

    @Named("mapInsPlacingToJson")
    default String mapInsPlacingToJson(List<BookingAccountModel.InsPlacing> insPlacing) {
        return insPlacing != null ? new Gson().toJson(insPlacing) : null;
    }

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
