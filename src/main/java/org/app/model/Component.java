package org.app.model;

public record Component(
        String key,
        boolean enabled,
        String qualifier,
        String name,
        String longName,
        String path
) {}
