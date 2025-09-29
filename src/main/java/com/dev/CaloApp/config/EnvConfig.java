package com.dev.CaloApp.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {
    private final Dotenv dotenv;

    public EnvConfig() {
        Dotenv tempDotenv = null;
        try {
            tempDotenv = Dotenv.load();
        } catch (Exception e) {
            tempDotenv = null;
        }
        this.dotenv = tempDotenv;
    }

    public String getGoogleClientId() {
        return dotenv != null ? dotenv.get("GOOGLE_CLIENT_ID") : System.getenv("GOOGLE_CLIENT_ID");
    }

    public String getGoogleClientSecret() {
        return dotenv != null ? dotenv.get("GOOGLE_CLIENT_SECRET") : System.getenv("GOOGLE_CLIENT_SECRET");
    }

    public String getGeminiApiKey() {
        return dotenv != null ? dotenv.get("GEMINI_API_KEY") : System.getenv("GEMINI_API_KEY");
    }

    public String getMailUsername() {
        return dotenv != null ? dotenv.get("MAIL_USERNAME") : System.getenv("MAIL_USERNAME");
    }

    public String getMailPassword() {
        return dotenv != null ? dotenv.get("MAIL_PASSWORD") : System.getenv("MAIL_PASSWORD");
    }

    public String getDbUsername() {
        return dotenv != null ? dotenv.get("DB_USERNAME") : System.getenv("DB_USERNAME");
    }

    public String getDbPassword() {
        return dotenv != null ? dotenv.get("DB_PASSWORD") : System.getenv("DB_PASSWORD");
    }

    public String getJwtSignerKey() {
        return dotenv != null ? dotenv.get("JWT_SIGNER_KEY") : System.getenv("JWT_SIGNER_KEY");
    }
}
