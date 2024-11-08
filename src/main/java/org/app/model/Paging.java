package org.app.model;

// Nested Records
public record Paging(
        int pageIndex,
        int pageSize,
        int total
) {
}
