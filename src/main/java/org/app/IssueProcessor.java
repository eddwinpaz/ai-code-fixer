package org.app;

import org.app.model.PromptResponse;
import org.app.model.SonarQubeIssues;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IssueProcessor {

    private final PromptService promptService;
    private final GitManager gitManager;
    private final FileUpdater fileUpdater;

    public IssueProcessor(ConfigLoader config) {
        this.promptService = new PromptService(config);
        this.gitManager = new GitManager();
        this.fileUpdater = new FileUpdater(config);
    }

    public void processIssues(List<SonarQubeIssues> issues) {
        issues.forEach(issue -> {
            CompletableFuture<PromptResponse> responseFuture;
            try {
                responseFuture = promptService.getPromptResponse(issue);
                responseFuture.thenAccept(response -> {
                    if (response != null) processGitOperation(issue, response);
                    else System.err.println("Failed to get a valid response.");
                });
            } catch (IOException e) {
                System.err.println("Failed to process issue: " + e.getMessage());
            }
        });
    }

    private void processGitOperation(SonarQubeIssues issue, PromptResponse response) {
        String branchName = formatIssueMessage(issue.message());
        String branch = String.format("%s/%s", issue.severity().toLowerCase(), branchName);
        try (var git = gitManager.openRepository()) {
            gitManager.createBranch(git, branch);
            fileUpdater.updateFile(issue, response);
            gitManager.commitChanges(git, String.format("Issue with severity %s in %s", issue.severity().toLowerCase(), branch));
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatIssueMessage(String message) {
        String cleanedMessage = message
                .replaceAll("[^a-zA-Z\\s]", "")
                .trim()
                .replaceAll("\\s{2,}", " ");
        return "issue/" + cleanedMessage.replaceAll("\\s", "-").toLowerCase();
    }
}
