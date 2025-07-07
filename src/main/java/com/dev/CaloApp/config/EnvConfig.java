package com.dev.CaloApp.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {
    private final Dotenv dotenv;

    public EnvConfig() {
        dotenv = Dotenv.load();
    }

    public String getGoogleClientId() {
        return dotenv.get("GOOGLE_CLIENT_ID");
    }

    public String getGoogleClientSecret() {
        return dotenv.get("GOOGLE_CLIENT_SECRET");
    }

    public String getGeminiApiKey() {
        return dotenv.get("GEMINI_API_KEY");
    }

    public String getMailUsername() {
        return dotenv.get("MAIL_USERNAME");
    }

    public String getMailPassword() {
        return dotenv.get("MAIL_PASSWORD");
    }

    public String getDbUsername() {
        return dotenv.get("DB_USERNAME");
    }

    public String getDbPassword() {
        return dotenv.get("DB_PASSWORD");
    }

    public String getJwtSignerKey() {
        return dotenv.get("JWT_SIGNER_KEY");
    }
}
