package com.alpha.service.service;

import com.alpha.service.model.ProcedureParamModel;
import com.alpha.service.model.procedure.UspBookingVisitParam;
import com.alpha.service.model.procedure.UspBookingVisitGetParam;
import com.alpha.service.repository.BookingVisitRepository;
import jakarta.persistence.ParameterMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookingVisitService {

    @Autowired
    private BookingVisitRepository repository;

    public Map<String, Object> callUspBookingVisit(UspBookingVisitParam param) {
        List<ProcedureParamModel> params = new ArrayList<>();

        // Convert p_visitDt to java.sql.Timestamp
        Timestamp ts = null;
        if (param.getP_visitDt() != null && !param.getP_visitDt().trim().isEmpty()) {
            ts = parseToTimestamp(param.getP_visitDt());
        }

        params.add(new ProcedureParamModel("p_visitCd", param.getP_visitCd(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_visitDt", ts, Timestamp.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_subject", param.getP_subject(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_subjectDtl", param.getP_subjectDtl(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_source", param.getP_source(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_bookCd", param.getP_bookCd(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_pthevidance", param.getP_pthevidance(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_user", param.getP_user(), String.class, ParameterMode.IN));

        params.add(new ProcedureParamModel("p_out_visitCd", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));

        return repository.callBookingVisitProcedure("USP_BOOKING_VISIT", params);
    }

    public Map<String, Object> callUspBookingVisitGet(UspBookingVisitGetParam param) {
        List<ProcedureParamModel> params = new ArrayList<>();
        params.add(new ProcedureParamModel("p_visitCd", param.getVisitCd(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_bookCd", param.getBookCd(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_clientName", param.getClientName(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_param1", param.getP_param1(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_param2", param.getP_param2(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_param3", param.getP_param3(), String.class, ParameterMode.IN));
        params.add(new ProcedureParamModel("p_actionType", param.getActionType(), String.class, ParameterMode.IN));

        params.add(new ProcedureParamModel("p_resultCode", null, Integer.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_message", null, String.class, ParameterMode.OUT));
        params.add(new ProcedureParamModel("p_resultJson", null, String.class, ParameterMode.OUT));

        return repository.callBookingVisitProcedure("USP_BOOKING_VISIT_GET", params);
    }

    private Timestamp parseToTimestamp(String input) {
        try {
            // Try ISO offsets first
            OffsetDateTime odt = OffsetDateTime.parse(input);
            return Timestamp.from(odt.toInstant());
        } catch (Exception ignored) {}
        try {
            // Try local date time ISO
            LocalDateTime ldt = LocalDateTime.parse(input);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignored) {}
        try {
            // Try common pattern yyyy-MM-dd HH:mm:ss
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(input, f);
            return Timestamp.valueOf(ldt);
        } catch (Exception ignored) {}
        // Fallback: now
        return Timestamp.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}
