package com.alpha.service.repository;


import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.service.BookingAccountSIUDService;
import com.alpha.service.util.ProcedureParamUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Created by: fkusu
 * Date: 12/15/2024
 */
@Repository
public class BookingAccountRepository {
    private final Logger logger = LoggerFactory.getLogger(BookingAccountRepository.class);


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

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

            if (param.getName().equalsIgnoreCase("p_premium_budget")){
                System.out.println("param.getName()===>0" + param.getValue());
                if (param.getValue().equals("")){
                    param.setValue(null);
                }
                System.out.println("param.getName()===>1" + param.getValue());
            }
            if (param.getName().equalsIgnoreCase("p_tot_members")){
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
            if (param.getName().equalsIgnoreCase("p_ins_placing")) {
                if (param.getValue() == null || param.getValue().toString().trim().isEmpty()) {
                    param.setValue("[]");
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

    @Transactional(readOnly = true)
    public int getNextBookingRev(String bookingCd) {
        String sql = """
        SELECT COALESCE(MAX(bbad.REV), 0) + 1
        FROM BK_COMPARATION_HDR bch
        JOIN BK_COMPARATION_DTL bcd ON bch.COMPARATIONCD = bcd.COMPARATIONCD
        JOIN BK_PLACING_HDR bph ON bcd.PLACINGCD = bph.PLACINGCD
        JOIN BK_BOOKING_ACCOUNT_DTL bbad ON bph.BOOKCD = bbad.BOOKCD
        WHERE bbad.BOOKCD = :bookingCd
          AND bch.SENDDT IS NOT NULL
    """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("bookingCd", bookingCd)
                .getSingleResult();

        return ((Number) result).intValue();
    }

    @Transactional(readOnly = true)
    public boolean isLastRevSent(String bookingCd) {
        String sql = """
        SELECT COUNT(1)
        FROM (
          SELECT bbad.REV, bch.SENDDT
          FROM BK_COMPARATION_HDR bch
          JOIN BK_COMPARATION_DTL bcd ON bch.COMPARATIONCD = bcd.COMPARATIONCD
          JOIN BK_PLACING_HDR bph ON bcd.PLACINGCD = bph.PLACINGCD
          JOIN BK_BOOKING_ACCOUNT_DTL bbad ON bph.BOOKCD = bbad.BOOKCD
          WHERE bbad.BOOKCD = :bookingCd
          ORDER BY bbad.REV DESC
          LIMIT 1
        ) x
        WHERE x.SENDDT IS NOT NULL
    """;
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("bookingCd", bookingCd)
                .getSingleResult();
        return ((Number) result).intValue() > 0;
    }

    public boolean isBookingRevExist(String bookCd, int rev) {
        String sql = "SELECT COUNT(*) FROM BK_BOOKING_ACCOUNT_DTL WHERE BOOKCD = :bookCd AND REV = :rev";
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("bookCd", bookCd)
                .setParameter("rev", rev)
                .getSingleResult();
        return ((Number) result).intValue() > 0;
    }

    public boolean isClientBookingOpen(String clientCode, int rev) {
        String sql = """
            SELECT 1
            FROM BK_BOOKING_ACCOUNT_HDR
            WHERE CLIENTCODE = :clientCode
            AND STATUS = '1'
            LIMIT 1
        """;
        List<?> result = entityManager.createNativeQuery(sql)
                .setParameter("clientCode", clientCode)
                .getResultList();
        return !result.isEmpty();
    }
}
