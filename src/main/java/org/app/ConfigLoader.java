package org.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getGitUsername() {
        return properties.getProperty("git.username");
    }

    public String getGitPassword() {
        return properties.getProperty("git.password");
    }

    public String getRepoPath() {
        return properties.getProperty("git.repoPath");
    }

    public String getSonarToken() {
        return properties.getProperty("sonar.token");
    }

    public String getSonarHost() {
        return properties.getProperty("sonar.host");
    }

    public String getSonarProjectKey() {
        return properties.getProperty("sonar.projectKey");
    }

    public String getLlmHost() {
        return properties.getProperty("llm.host");
    }

    public String getLlmModel() {
        return properties.getProperty("llm.model");
    }

}
