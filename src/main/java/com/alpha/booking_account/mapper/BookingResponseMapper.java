package com.alpha.booking_account.mapper;


import com.alpha.booking_account.model.BookingAccountModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/18/2024
 */

public class  BookingResponseMapper {

    public BookingAccountModel mapToBookingAccount(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        BookingAccountModel model = new BookingAccountModel();
        model.setBookCd(objectToString(map.get("BOOKCD")));
        model.setClientCode(objectToString(map.get("CLIENTCODE")));
        model.setClientName(objectToString(map.get("CLIENTNAME")));
        model.setMktId(objectToString(map.get("MKTID")));
        model.setFullName(objectToString(map.get("FULLNAME")));
        model.setBookDate(objectToString(map.get("BOOKDT")));
        model.setPrevIns(objectToString(map.get("PREVINS")));
        model.setInsName(objectToString(map.get("INSNAME")));
        model.setExpDate(objectToString(map.get("EXPDT")));
        model.setPremiumBudget(objectToString(map.get("PREMIUMBGT")));
        model.setIp(objectToString(map.get("IP")));
        model.setOp(objectToString(map.get("OP")));
        model.setDt(objectToString(map.get("DT")));
        model.setMt(objectToString(map.get("MT")));
        model.setGl(objectToString(map.get("GL")));
        model.setDescription(objectToString(map.get("DESCRIPTION")));
        model.setStatus(objectToString(map.get("STATUS")));
        model.setDocTypes(mapDocTypeList(map.get("docTypes")));
        model.setCreatedBy(objectToString(map.get("createdBy")));
        model.setActionType(objectToString(map.get("actionType")));

        return model;
    }

    public List<BookingAccountModel> mapToBookingAccountList(List<Map<String, Object>> mapList) {
        if (mapList == null || mapList.isEmpty()) {
            return new ArrayList<>();
        }
        return mapList.stream()
                .map(this::mapToBookingAccount)
                .collect(Collectors.toList());
    }

    public BookingAccountModel.DocType mapDocType(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        BookingAccountModel.DocType docType = new BookingAccountModel.DocType();
        docType.setCode(objectToString(map.get("code")));
        docType.setRevDoc(objectToString(map.get("revDoc")));
        docType.setDescp(objectToString(map.get("descp")));
        docType.setUrlPath(objectToString(map.get("urlPath")));

        return docType;
    }

    public List<BookingAccountModel.DocType> mapDocTypeList(Object value) {
        if (value instanceof List<?>) {
            return ((List<?>) value).stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> mapDocType((Map<String, Object>) item))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private String objectToString(Object obj) {
        return obj != null ? obj.toString() : null;
    }
}