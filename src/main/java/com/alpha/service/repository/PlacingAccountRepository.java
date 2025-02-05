package com.alpha.service.repository;


import com.alpha.service.model.ProcedureParamModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by: fkusu
 * Date: 2/3/2025
 */
@Repository
public class PlacingAccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> callPlacingProcedure(String procedureName, List<ProcedureParamModel> params) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(procedureName);

        for (ProcedureParamModel param : params) {
            query.registerStoredProcedureParameter(param.getName(), param.getType(), param.getMode());
            if (param.getMode() == ParameterMode.IN) {
                query.setParameter(param.getName(), param.getValue());
            }
        }

        query.execute();

        Map<String, Object> result = new HashMap<>();
        for (ProcedureParamModel param : params) {
            if (param.getMode() == ParameterMode.OUT) {
                result.put(param.getName(), query.getOutputParameterValue(param.getName()));
            }
        }

        return result;
    }
}
