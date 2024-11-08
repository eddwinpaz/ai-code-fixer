package org.app.model;

public record TextRange(
        int startLine,
        int endLine,
        int startOffset,
        int endOffset
) {
}
