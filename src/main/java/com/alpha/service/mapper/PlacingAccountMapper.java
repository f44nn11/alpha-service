package com.alpha.service.mapper;


import com.alpha.service.model.placing.PlacingRequestModel;
import com.alpha.service.model.procedure.UspPlacingAccountDtlParam;
import com.alpha.service.model.procedure.UspPlacingAccountParam;
import com.google.gson.Gson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/*
 * Created by: fkusu
 * Date: 2/3/2025
 */
@Mapper
public interface PlacingAccountMapper {
    PlacingAccountMapper INSTANCE = Mappers.getMapper(PlacingAccountMapper.class);

    @Mapping(target = "placingCd", source = "placingCd")
    @Mapping(target = "bookCd", source = "bookCd")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "insPlacing", source = "insurances", qualifiedByName = "mapInsPlacingToJson")
    UspPlacingAccountParam toUspPlacingAccountParam(PlacingRequestModel model);

    @Named("mapInsPlacingToJson")
    default String mapInsPlacingToJson(List<PlacingRequestModel.Insurance> insurances) {
        return insurances != null ? new Gson().toJson(insurances) : "[]";
    }
}
