package org.app;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.app.model.Issue;
import org.app.model.SonarIssues;
import org.app.model.SonarQubeIssues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SonarQubeApi {
    private final OkHttpClient client;
    private final Gson gson;
    private final String sonarQubeUrl;
    private final String token;
    private final String projectKey;

    public SonarQubeApi(ConfigLoader config) {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.sonarQubeUrl = config.getSonarHost();
        this.token = config.getSonarToken();
        this.projectKey = config.getSonarProjectKey();
    }

    public List<SonarQubeIssues> getIssues() {
        String url = String.format("%s/api/issues/search?componentKeys=%s", sonarQubeUrl, projectKey);
        Request request = createRequest(url);
        try (Response response = client.newCall(request).execute()) {
            return parseResponse(response);
        } catch (IOException e) {
            System.err.println(STR."Error fetching issues: \{e.getMessage()}");
            throw new RuntimeException(e);
        }
    }

    private Request createRequest(String url) {
        String encodedToken = Base64.getEncoder().encodeToString((STR."\{token}:").getBytes());
        String credential = String.format("Basic %s", encodedToken);
        return new Request.Builder()
                .method("GET", null)
                .url(url)
                .header("Authorization", credential)
                .build();
    }

    private List<SonarQubeIssues> parseResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException(STR."Unexpected HTTP code: \{response}");
        }
        String responseBody = response.body().string();
        SonarIssues sonarIssues = gson.fromJson(responseBody, SonarIssues.class);
        return extractIssues(sonarIssues);
    }

    private List<SonarQubeIssues> extractIssues(SonarIssues sonarIssues) {
        List<SonarQubeIssues> issues = new ArrayList<>();
        if (sonarIssues.total() > 0) {
            for (Issue issue : sonarIssues.issues()) {
                if (issue.textRange() != null) {
                    SonarQubeIssues sonarQubeIssues = new SonarQubeIssues(
                            issue.component(),
                            issue.severity(),
                            issue.message(),
                            issue.textRange().startLine(),
                            issue.textRange().endLine()
                    );
                    issues.add(sonarQubeIssues);
                }
            }
        }
        return issues;
    }
}

