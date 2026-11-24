package com.fight.app.data.api;

import java.util.HashMap;
import java.util.Map;

/**
 * API for reporting fighters or bad behavior (FR-015).
 */
public final class ReportApi {

    private ReportApi() {}

    public static void submitReport(
            String token,
            int targetId,
            String reason,
            String details,
            ApiCallback<Map> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("target_id", targetId);
        body.put("reason", reason);
        if (details != null) body.put("details", details);
        body.put("severity", "Medium");

        ApiClient.post("/reports/", body, token, Map.class, callback);
    }
}
