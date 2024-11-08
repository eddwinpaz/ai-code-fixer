package org.app.model;

// Record for input prompt
public record PromptRequest(String model, String prompt, boolean stream) {}
