package org.app.model;

import java.util.List;

// Root Record
public record SonarIssues(
        int total,
        int p,
        int ps,
        Paging paging,
        int effortTotal,
        List<Issue> issues,
        List<Component> components,
        List<Object> facets
) {
}
