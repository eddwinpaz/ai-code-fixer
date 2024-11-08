package org.app;

import org.app.model.PromptRequest;
import org.app.model.PromptResponse;
import org.app.model.SonarQubeIssues;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Main {

    private static ConfigLoader config;

    public static void main(String[] args) {
        config = new ConfigLoader();
        SonarQubeApi sonarQubeApi = new SonarQubeApi(config);
        List<SonarQubeIssues> issues = sonarQubeApi.getIssues();
        AiAgentIssueProcessor(issues);
    }

    private static void AiAgentIssueProcessor(List<SonarQubeIssues> issues) {
        issues.forEach(issue -> {
            CompletableFuture<PromptResponse> responseFuture = null;
            try {
                responseFuture = getPromptResponseCompletableFuture(issue);
                responseFuture.thenAccept(response -> {
                    if (response != null) gitProcessFiles(issue, response);
                    else System.err.println("Failed to get a valid response.");
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private static void gitProcessFiles(SonarQubeIssues issue, PromptResponse response) {
        GitManager gitManager = new GitManager();
        String branchName = formatIssueMessage(issue.message());
        String branch = String.format("issue/%s/%s", issue.severity().toLowerCase(), branchName);

        System.out.println(branch);

        try (Git git = gitManager.openRepository()) {
            gitManager.createBranch(git, branch);

            FileManager file = new FileManager(config.getRepoPath());
            String filePath = extractFilePath(issue.component());

            System.out.println(filePath);

            file.writeFile(filePath, cleanCodeResponse(response.response()));
            //            file.modifyFile(extractFilePath(issue.component()), issue.startLine(), issue.endLine(), response.response());

            gitManager.commitChanges(git, String.format("Issue with severity %s in %s", issue.severity().toLowerCase(), branch));
            //gitManager.pushBranch(git, branchName);

        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static String cleanCodeResponse(String response) {
        return response.replaceAll("(?s)```\\w*\\n|```", "").trim();
    }


    public static String formatIssueMessage(String message) {
        // Step 1: Remove numbers, special characters, and extra spaces
        String cleanedMessage = message
                .replaceAll("[^a-zA-Z\\s]", "") // Remove numbers and special characters
                .trim() // Remove leading and trailing spaces
                .replaceAll("\\s{2,}", " "); // Replace multiple spaces with a single space

        // Step 2: Replace spaces between words with hyphens and convert to lowercase
        return STR."issue/\{cleanedMessage.replaceAll("\\s", "-").toLowerCase()}";
    }


    private static String extractFilePath(String component) {
        return component.split(":")[1];
    }


    private static String getCodeFromRepositoryFile(String filePath, int startLine, int endLine) throws IOException {
        String extractedPath = String.format("%s", extractFilePath(filePath));
        System.out.println(extractedPath);
        FileManager file = new FileManager(config.getRepoPath());
        return file.readFile(extractedPath);
//        return file.readLines(extractedPath, startLine, endLine);
    }

    private static CompletableFuture<PromptResponse> getPromptResponseCompletableFuture(SonarQubeIssues issue) throws IOException {
        AIAgent agent = new AIAgent(config);
        String code = getCodeFromRepositoryFile(issue.component(), issue.startLine(), issue.endLine());

        String promptMessage = String.format(
                "You are an AI coding specialist. SonarQube has detected an issue with the following details: %s. The current code is:\n\n'''%s'''\n\nYour task: Apply the best approach to correct the issue. **Only return the corrected code** in a format ready to replace the original. If an explanation is necessary, include it briefly as a comment within the code, but keep the comment minimal and specific. Do not add any extra text outside the code itself.",
                issue.message(),
                code
        );
        System.out.println(promptMessage);
        PromptRequest prompt = new PromptRequest(config.getLlmModel(), promptMessage, false);
        return agent.sendPromptAsync(prompt);
    }
}
