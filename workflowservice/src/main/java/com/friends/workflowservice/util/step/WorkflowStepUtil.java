package com.friends.workflowservice.util.step;

import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.Optional;

@NoArgsConstructor
public class WorkflowStepUtil {

    /**
     * Normalizes a step code by trimming and converting to uppercase.
     *
     * @param stepCode the step code to normalize
     * @return the normalized step code or null if input is null
     */
    public static String normalizeStepCode(String stepCode) {
        return Optional.ofNullable(stepCode)
                .map(String::trim)
                .map(code -> code.toUpperCase(Locale.ROOT))
                .orElse(null);
    }
}

