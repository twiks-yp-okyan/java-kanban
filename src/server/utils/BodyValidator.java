package server.utils;

import com.google.gson.JsonElement;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class BodyValidator {
    private static final List<BodyValidationResult> validationResults = new ArrayList<>();

    public static List<BodyValidationResult> getValidationResults() {
        return validationResults;
    }

    private static void validateId(JsonElement body) {
        if (body.getAsJsonObject().get("id") == null) {
            validationResults.add(BodyValidationResult.ID_NULL);
        } else {
            try {
                body.getAsJsonObject().get("id").getAsInt();
            } catch (NumberFormatException e) {
                validationResults.add(BodyValidationResult.ID_NOT_INT);
            }
        }
    }

    public static void validateStatus(JsonElement body) {
        if (body.getAsJsonObject().get("status") == null) {
            validationResults.add(BodyValidationResult.STATUS_NULL);
        } else {
            try {
                TaskStatus.valueOf(body.getAsJsonObject().get("status").getAsString());
            } catch (IllegalArgumentException e) {
                validationResults.add(BodyValidationResult.STATUS_UNKNOWN);
            }
        }
    }
}
