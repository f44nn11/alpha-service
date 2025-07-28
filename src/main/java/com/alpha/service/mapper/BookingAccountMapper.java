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


    @Mapping(target = "clientCode", source = "clientCode.code")
    @Mapping(target = "mktId", source = "mktId.code")
    @Mapping(target = "prevIns", source = "prevIns.code")
    @Mapping(target = "effDate", source = "effDate")
    UspBookingAccountParam toUspBookingAccountParam(BookingAccountModel model);



    @Mapping(target = "bookCd", source = "bookCd")
    @Mapping(target = "revDoc",
            expression = "java(model.getBookRev() != null ? model.getBookRev() : docType.getRevDoc())")
    @Mapping(target = "descriptionDtl", source = "model.descriptionDtl")
    @Mapping(target = "premiumBudget", source = "model.premiumBudget")
    @Mapping(target = "totMembers", source = "model.totMembers")
    @Mapping(target = "ip", source = "model.ip")
    @Mapping(target = "op", source = "model.op")
    @Mapping(target = "dt", source = "model.dt")
    @Mapping(target = "mt", source = "model.mt")
    @Mapping(target = "gl", source = "model.gl")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "actionType", expression = "java(docType.getActionType() != null && !docType.getActionType().isBlank() ? model.getActionType() : model.getActionType())")
    @Mapping(target = "insPlacing", source = "model.insPlacing", qualifiedByName = "mapInsPlacingToJson")
    @Mapping(target = "docTypes", source = "model.docTypes", qualifiedByName = "mapDocTypesToJson")
    UspBookingAccountDtlParam toUspBookingAccountDtlParam(
            String bookCd,
            BookingAccountModel.DocType docType,
            BookingAccountModel model,
            String createdBy
    );

    @Named("mapInsPlacingToJson")
    default String mapInsPlacingToJson(List<BookingAccountModel.InsPlacing> insPlacing) {
        return insPlacing != null ? new Gson().toJson(insPlacing) : null;
    }
    @Named("mapDocTypesToJson")
    default String mapDocTypesToJson(List<BookingAccountModel.DocType> docTypes) {
        return docTypes != null ? new Gson().toJson(docTypes) : "[]";
    }
}
