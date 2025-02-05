package com.alpha.service.mapper;

import com.alpha.service.model.ClientMasterModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * Created by: fkusu
 * Date: 12/4/2024
 */
public class ClientMasterMapper {
 public static List<ClientMasterModel> mapToClientMasterList(List<Map<String, Object>> data) {
  return data.stream()
          .map(ClientMasterMapper::mapToClientMaster) // Panggil method map untuk setiap item
          .collect(Collectors.toList());
 }

 private static ClientMasterModel mapToClientMaster(Map<String, Object> map) {
  ClientMasterModel clientMaster = new ClientMasterModel();
  clientMaster.setClientCode((String) map.get("CLIENTCODE"));
  clientMaster.setClientType((String) map.get("CLIENTTYPE"));
  clientMaster.setClientName((String) map.get("CLIENTNAME"));
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
  clientMaster.setLobId((String) map.get("LOBID"));
  clientMaster.setClientGroup((String) map.get("CLIENTGROUP"));
  clientMaster.setCreateBy((String) map.get("CREATEBY"));

//  // Konversi tanggal ke LocalDateTime jika formatnya sesuai
//  clientMaster.setCreateDt(map.get("CREATEDT") != null ?
//          LocalDateTime.parse(map.get("CREATEDT").toString()) : null);
//  clientMaster.setEditDt(map.get("EDITDT") != null ?
//          LocalDateTime.parse(map.get("EDITDT").toString()) : null);

  return clientMaster;
 }
}
