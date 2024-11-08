package org.app.model;

import java.util.List;

public record Issue(
        String key,
        String rule,
        String severity,
        String component,
        String project,
        String hash,
        TextRange textRange,
        List<Object> flows,
        String resolution,
        String status,
        String message,
        String effort,
        String debt,
        String author,
        List<String> tags,
        String creationDate,
        String updateDate,
        String closeDate,
        String type,
        String scope,
        boolean quickFixAvailable,
        List<Object> messageFormattings,
        List<Object> codeVariants,
        String cleanCodeAttribute,
        String cleanCodeAttributeCategory,
        List<Impact> impacts,
        String issueStatus,
        boolean prioritizedRule
) {
}
