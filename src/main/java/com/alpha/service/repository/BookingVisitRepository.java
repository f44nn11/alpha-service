package com.alpha.service.repository;

import com.alpha.service.model.ProcedureParamModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookingVisitRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> callBookingVisitProcedure(String procedureName, List<ProcedureParamModel> params) {
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
