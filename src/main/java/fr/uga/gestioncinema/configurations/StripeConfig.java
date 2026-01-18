package fr.uga.gestioncinema.configurations;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe Payment Gateway Configuration
 * Initializes Stripe SDK with API keys from application.yml
 */
@Configuration
@Slf4j
@Getter
public class StripeConfig {

    /**
     * Stripe Secret API Key (server-side)
     * Format: sk_test_XXXXX (test) or sk_live_XXXXX (production)
     */
    @Value("${stripe.api-key}")
    private String apiKey;

    /**
     * Stripe Webhook Secret for signature verification
     * Format: whsec_XXXXX
     */
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    /**
     * Default currency for payments (EUR, USD, etc.)
     */
    @Value("${stripe.currency:EUR}")
    private String currency;

    /**
     * Initialize Stripe SDK with API key
     * Called automatically after bean construction
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = this.apiKey;
        
        // Log initialization (without exposing full key for security)
        String maskedKey = maskApiKey(this.apiKey);
        log.info("✓ Stripe SDK initialized with API key: {}", maskedKey);
        log.info("✓ Default currency: {}", this.currency);
        log.info("✓ Webhook secret configured: {}", this.webhookSecret != null ? "Yes" : "No");
        
        // Warn if using test keys in production
        if (this.apiKey.startsWith("sk_live_")) {
            log.warn("⚠️  PRODUCTION Stripe API key detected! Ensure proper security measures.");
        } else if (this.apiKey.startsWith("sk_test_")) {
            log.info("ℹ️  TEST mode: Using Stripe test API key");
        }
    }

    /**
     * Mask API key for logging (show only first/last chars)
     * Example: sk_test_51QX...xyz
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "***";
        }
        String prefix = apiKey.substring(0, Math.min(12, apiKey.length()));
        String suffix = apiKey.substring(Math.max(apiKey.length() - 3, 12));
        return prefix + "..." + suffix;
    }
}
