package org.app.model;

// Record for handling response
public record PromptResponse(String response, boolean done) {}
/*
        {
        "model": "llama3.1:8b",
        "created_at": "2024-11-06T19:13:12.958826291Z",
        "response": "A",
        "done": false
        }
*/