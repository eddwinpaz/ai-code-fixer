package org.app;

import com.google.gson.Gson;
import okhttp3.*;
import org.app.model.PromptRequest;
import org.app.model.PromptResponse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AIAgent {
    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;
    private final ExecutorService executor;

    public AIAgent(ConfigLoader config) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS) // Wait indefinitely for the response
                .build();
        this.gson = new Gson();
        this.baseUrl = config.getLlmHost();
        this.executor = Executors.newFixedThreadPool(10); // Thread pool for async requests
    }

    public CompletableFuture<PromptResponse> sendPromptAsync(PromptRequest promptRequest) {
        RequestBody body = RequestBody.create(gson.toJson(promptRequest), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(STR."\{baseUrl}/api/generate")
                .post(body)
                .build();
        return CompletableFuture.supplyAsync(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    return gson.fromJson(responseBody, PromptResponse.class);
                } else {
                    throw new IOException(STR."Unexpected HTTP code: \{response}");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
}
