package org.app;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;

public class GitManager {

    private final String repoPath;
    private final String username;
    private final String password;

    // Constructor that uses ConfigLoader to set values from config.properties
    public GitManager() {
        ConfigLoader config = new ConfigLoader();
        this.repoPath = config.getRepoPath();
        this.username = config.getGitUsername();
        this.password = config.getGitPassword();
    }

    // Initialize Git repository
    public Git openRepository() throws IOException {
        return Git.open(Paths.get(repoPath).toFile());
    }

    // Create a new branch
    public void createBranch(@NotNull Git git, String branchName) throws GitAPIException {
        System.out.println(STR."Creating branch: \{branchName}");
        git.branchCreate().setName(branchName).call();
        git.checkout().setName(branchName).call();
    }

    // Commit changes
    public void commitChanges(@NotNull Git git, String commitMessage) throws GitAPIException {
        System.out.println(STR."Committing changes: \{commitMessage}");
        git.add().addFilepattern(".").call(); // Stage all changes
        git.commit().setMessage(commitMessage).call();
    }

    // Push the branch to origin
    public void pushBranch(@NotNull Git git, String branchName) throws GitAPIException {
        git.push()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setRemote(Constants.DEFAULT_REMOTE_NAME)
                .add(branchName)
                .call();
    }
}
