# 💳 Payment Architecture - Technical Notes

## 📋 Overview

This document explains the technical decisions and architecture for the Stripe payment integration in the Cinema API.

---

## 🏗️ Architecture Pattern

### **Standard Controllers (OpenAPI-First) ✅**

All controllers in this project follow the **API-First** pattern:

```
1. Define API in YAML (OpenAPI 3.0)
2. Generate models & interfaces (openapi-generator-maven-plugin)
3. Implement generated interface in Controller
4. Map OpenAPI models ↔ DTOs ↔ Entities
```

**Example:**
```
city.yaml → CityApi (generated) → CityController (implements CityApi)
```

---

## ⚠️ Exception: Stripe Webhook Controller

### **Why `StripeWebhookController` Doesn't Follow OpenAPI Pattern?**

The `StripeWebhookController` is **intentionally NOT generated from OpenAPI** for these technical reasons:

#### 1. **Raw Payload Requirement** 🔒

Stripe webhooks require **signature verification** for security:

```java
Event event = Webhook.constructEvent(
    payload,        // ← Must be RAW string
    signature,      // ← From HTTP header
    webhookSecret   // ← Your secret
);
```

**Problem:** OpenAPI binding automatically parses JSON into objects, which breaks signature verification.

#### 2. **Security Implications** 🛡️

Without raw payload access:
- ❌ Cannot verify webhook signature
- ❌ Vulnerable to fake webhook attacks
- ❌ Attacker could send fake "payment_succeeded" events

#### 3. **Spring Boot Request Body Consumption** 📦

When Spring parses JSON:
```java
@RequestBody MyModel model  // ← JSON already consumed & parsed
```

You **cannot** re-read the raw body for signature verification.

---

## 📁 File Structure

```
src/main/
├── resources/apifile/
│   └── payment.yaml           ← OpenAPI spec (payment endpoints only)
│
└── java/.../web/
    ├── PaymentController.java        ✅ Generated from OpenAPI
    │   ├── POST /payment/create-payment-intent
    │   ├── GET /payment/intent/{id}
    │   └── GET /payment/reservation/{id}
    │
    └── StripeWebhookController.java  ⚠️  NOT from OpenAPI (by design)
        └── POST /payment/webhook (raw payload handling)
```

---

## 🔄 Payment Flow

### **1. Create Payment Intent (OpenAPI Controller)**

```http
POST /api/payment/create-payment-intent
Authorization: Bearer {JWT}
Content-Type: application/json

{
  "reservationId": 789
}
```

**Flow:**
1. `PaymentController` (implements `PaymentApi`)
2. Maps `CreatePaymentIntentRequest` (OpenAPI model) → internal DTO
3. Calls `PaymentService.createPaymentIntent()`
4. Stripe SDK creates PaymentIntent
5. Returns `PaymentIntentResponse` (OpenAPI model)

---

### **2. Webhook Confirmation (Manual Controller)**

```http
POST /api/payment/webhook
Stripe-Signature: t=1234567890,v1=xxxxx
Content-Type: application/json

{raw JSON event from Stripe}
```

**Flow:**
1. `StripeWebhookController` (manual @RestController)
2. Receives **raw String payload** + signature header
3. Verifies signature with `Webhook.constructEvent()`
4. Parses event type (`payment_intent.succeeded`, etc.)
5. Updates database (payment + reservation)
6. Returns `200 OK` to Stripe

---

## 📊 Comparison Table

| Feature | PaymentController | StripeWebhookController |
|---------|-------------------|-------------------------|
| **Source** | Generated from `payment.yaml` | Manual `@RestController` |
| **Models** | OpenAPI models (`PaymentIntentResponse`) | Raw `String` + Stripe SDK models |
| **Pattern** | Implements `PaymentApi` interface | Direct `@PostMapping` |
| **Authentication** | OAuth2 JWT required | Public endpoint (signature verified) |
| **Payload** | Parsed JSON objects | Raw string for signature |
| **Documentation** | Auto-generated from YAML | Manual `@Operation` annotations |

---

## 🔐 Security Considerations

### **Webhook Security** 🛡️

```java
// ✅ CORRECT: Verify signature with raw payload
Event event = Webhook.constructEvent(payload, signature, secret);

// ❌ WRONG: Parse JSON first, then try to verify
MyModel model = parseJson(payload);
// Too late! Cannot verify signature now
```

### **Why Webhook Endpoint is Public**

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/payment/webhook").permitAll()  // ← No JWT required
            .anyRequest().authenticated()
        );
    }
}
```

**Reason:**
- Stripe servers call this endpoint (not frontend)
- Stripe doesn't have our JWT token
- Security is ensured by **signature verification** instead

---

## 📝 Documentation Strategy

### **OpenAPI Endpoints**

Documented in `payment.yaml`:
- Visible in Swagger UI
- Client SDK generation
- Contract-first development

### **Webhook Endpoint**

Documented with **Swagger annotations** in code:
```java
@Operation(
    summary = "Stripe Webhook Handler",
    description = "Receives and processes Stripe webhook events"
)
@ApiResponses(...)
```

**Result:** Visible in Swagger UI, but not in OpenAPI spec file.

---

## 🎯 Best Practices Applied

### ✅ Do's

1. **Use OpenAPI for user-facing APIs** (payment intent, get payment)
2. **Use manual controller for webhooks** (signature verification)
3. **Document webhook with @Operation** (Swagger visibility)
4. **Verify webhook signature ALWAYS** (security)
5. **Return 200 OK immediately** (Stripe retry logic)

### ❌ Don'ts

1. **Don't use OpenAPI for webhooks** (breaks signature verification)
2. **Don't parse webhook JSON first** (need raw payload)
3. **Don't skip signature verification** (security vulnerability)
4. **Don't perform long operations in webhook** (Stripe timeout)
5. **Don't return non-200 for valid webhooks** (causes retries)

---

## 🧪 Testing Webhooks

### **Local Development**

```bash
# Use Stripe CLI to forward webhooks
stripe listen --forward-to http://localhost:8082/api/payment/webhook

# Trigger test webhook
stripe trigger payment_intent.succeeded
```

### **Production**

1. Configure webhook endpoint in Stripe Dashboard
2. Use ngrok or similar for HTTPS
3. Set webhook secret in `application.yml`
4. Monitor webhook deliveries in Stripe Dashboard

---

## 🔗 References

- **Stripe Webhooks Guide**: https://stripe.com/docs/webhooks
- **Signature Verification**: https://stripe.com/docs/webhooks/signatures
- **OpenAPI Generator**: https://openapi-generator.tech/
- **Spring Boot Security**: https://spring.io/guides/topicals/spring-security-architecture

---

## 📞 Questions?

If you're adding new payment endpoints:

1. **User-initiated actions** (create payment, get status)
   → Add to `payment.yaml` ✅

2. **Stripe-initiated callbacks** (webhooks)
   → Add to `StripeWebhookController` ⚠️

3. **Unsure?**
   → Ask: "Does this need raw payload for signature verification?"

---

**Last Updated:** 2024-12-21
**Author:** Cinema API Team









