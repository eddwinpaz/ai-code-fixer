package org.app.model;

public record SonarQubeIssues(
        String component,
        String severity,
        String message,
        int startLine,
        int endLine
) {
}
