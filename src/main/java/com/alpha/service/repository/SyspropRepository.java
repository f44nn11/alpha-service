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
public class SyspropRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> callStoredProcedure(String procedureName, List<ProcedureParamModel> params) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(procedureName);

        // Daftarkan parameter
        for (ProcedureParamModel param : params) {
            query.registerStoredProcedureParameter(param.getName(), param.getType(), param.getMode());
            query.setParameter(param.getName(), param.getValue());
        }

        // Jalankan prosedur
        return query.getResultList();
    }
    public Map<String, Object> callSyspropInsUpdProcedure(String procedureName, List<ProcedureParamModel> params) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(procedureName);

        // Daftarkan parameter
        for (ProcedureParamModel param : params) {
            System.out.println("param==>" + param.getName() + "=" + param.getValue());
            query.registerStoredProcedureParameter(param.getName(), param.getType(), param.getMode());
            if (param.getMode() == ParameterMode.IN) {
                query.setParameter(param.getName(), param.getValue());
            }
        }

        // Jalankan prosedur
        query.execute();

        // Ambil nilai parameter OUT
        Map<String, Object> result = new HashMap<>();
        for (ProcedureParamModel param : params) {
            if (param.getMode() == ParameterMode.OUT) {
                result.put(param.getName(), query.getOutputParameterValue(param.getName()));
            }
        }

        return result;
    }
}
