package com.alpha.service.repository;


import com.alpha.service.model.EmailCheckResult;
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

//    @Transactional(rollbackFor = Exception.class)
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

    public EmailCheckResult checkEmailAlready(String code, String param1, String param2,String param3, String actionType) {
        StoredProcedureQuery sp = entityManager.createStoredProcedureQuery("USP_CHECK_EMAIL")
                .registerStoredProcedureParameter("p_code", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_param1", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_param2", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_param3", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_actionType", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("p_hasEmail", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_resultCode", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_email", String.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("p_message", String.class, ParameterMode.OUT);

        sp.setParameter("p_code", code);
        sp.setParameter("p_param1", param1);
        sp.setParameter("p_param2", param2);
        sp.setParameter("p_param3", param3);
        sp.setParameter("p_actionType", actionType);

        sp.execute();

        int hasEmail = (Integer) sp.getOutputParameterValue("p_hasEmail");
        int resultCode = (Integer) sp.getOutputParameterValue("p_resultCode");
        String email = (String) sp.getOutputParameterValue("p_email");
        String message = (String) sp.getOutputParameterValue("p_message");

        return new EmailCheckResult(hasEmail, resultCode, email, message);
    }

    public int getNextProposalRev(String placingCd, Long insCd) {
        String sql = """
                    SELECT COALESCE(MAX(REV), 0) + 1
                    FROM BK_PROPOSAL_REQUEST_DTL 
                    WHERE PLACINGCD = :placingCd AND INSCD = :insCd
                """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("placingCd", placingCd)
                .setParameter("insCd", insCd)
                .getSingleResult();

        return ((Number) result).intValue();
    }

    public boolean isBookCdPlaced(String bookCd) {
        String sql = """
            SELECT 1
            FROM BK_PLACING_HDR
            WHERE BOOKCD = :bookCd
            LIMIT 1
        """;
        List<?> result = entityManager.createNativeQuery(sql)
                .setParameter("bookCd", bookCd)
                .getResultList();
        return !result.isEmpty();
    }
}
