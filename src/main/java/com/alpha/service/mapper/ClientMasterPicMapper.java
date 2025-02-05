package com.alpha.service.mapper;

import com.alpha.service.model.ClientMasterPicModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/4/2024
 */
public class ClientMasterPicMapper {
    public static List<ClientMasterPicModel> mapToClientMasterList(List<Map<String, Object>> data) {
        return data.stream()
                .map(ClientMasterPicMapper::mapToClientPicMaster)
                .collect(Collectors.toList());
    }

    private static ClientMasterPicModel mapToClientPicMaster(Map<String, Object> map) {
        ClientMasterPicModel clientMaster = new ClientMasterPicModel();
        clientMaster.setClientCode((String) map.get("CLIENTCODE"));
        clientMaster.setPicType((String) map.get("PICTYPE"));
        clientMaster.setPicName((String) map.get("PICNAME"));
        clientMaster.setPicTitle((String) map.get("PICTITLE"));
        clientMaster.setSameAddr((String) map.get("SAMEADDR"));
        clientMaster.setBuilding((String) map.get("BUILDING"));
        clientMaster.setStreet1((String) map.get("STREET1"));
        clientMaster.setStreet2((String) map.get("STREET2"));
        clientMaster.setStreet3((String) map.get("STREET3"));
        clientMaster.setCountryId((String) map.get("COUNTRYID"));
        clientMaster.setProvinceId((String) map.get("PROVINCEID"));
        clientMaster.setCityId((String) map.get("CITYID"));
        clientMaster.setStateId((String) map.get("STATEID"));
        clientMaster.setDistrictId((String) map.get("DISTRICTID"));
        clientMaster.setZipCode((String) map.get("ZIPCODE"));
        clientMaster.setPhone1((String) map.get("PHONE1"));
        clientMaster.setPhone2((String) map.get("PHONE2"));
        clientMaster.setFax1((String) map.get("FAX1"));
        clientMaster.setFax2((String) map.get("FAX2"));
        clientMaster.setEmail((String) map.get("EMAIL"));
        clientMaster.setDob((String) map.get("DOB"));
        clientMaster.setSubGroup((String) map.get("SUBGROUP"));
        clientMaster.setCreateBy((String) map.get("CREATEBY"));

//  // Konversi tanggal ke LocalDateTime jika formatnya sesuai
//  clientMaster.setCreateDt(map.get("CREATEDT") != null ?
//          LocalDateTime.parse(map.get("CREATEDT").toString()) : null);
//  clientMaster.setEditDt(map.get("EDITDT") != null ?
//          LocalDateTime.parse(map.get("EDITDT").toString()) : null);

        return clientMaster;
    }
}
