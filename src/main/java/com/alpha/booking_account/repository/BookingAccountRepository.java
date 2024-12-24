package com.alpha.booking_account.repository;


import com.alpha.booking_account.entity.ClientMasterPic;
import com.alpha.booking_account.model.ProcedureParamModel;
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
 * Date: 12/15/2024
 */
@Repository
public class BookingAccountRepository {
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

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> callBookingAccountSIUDProcedure(String procedureName, List<ProcedureParamModel> params) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(procedureName);

        // Daftarkan parameter
        for (ProcedureParamModel param : params) {
            if (param.getName().equalsIgnoreCase("p_book_date")){
                if (param.getValue().equals("")){
                    param.setValue(null);
                }
            }
            if (param.getName().equalsIgnoreCase("p_exp_date")){
                if (param.getValue().equals("")){
                    param.setValue(null);
                }
            }
            if (param.getName().equalsIgnoreCase("p_premium_budget")){
                System.out.println("param.getName()===>0" + param.getValue());
                if (param.getValue().equals("")){
                    param.setValue(null);
                }
                System.out.println("param.getName()===>1" + param.getValue());
            }
            if (param.getName().equalsIgnoreCase("p_ip")
                    || param.getName().equalsIgnoreCase("p_op")
                    || param.getName().equalsIgnoreCase("p_dt")
                    || param.getName().equalsIgnoreCase("p_mt")
                    || param.getName().equalsIgnoreCase("p_gl")){
                if (param.getValue().equals("")){
                    param.setValue(0);
                }
            }
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
