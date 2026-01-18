# 🧪 Stripe Testing Guide

## 🚀 Quick Start

### 1. Get Your Stripe Test Keys

1. **Sign up for Stripe**: https://dashboard.stripe.com/register
2. **Get test API keys**: https://dashboard.stripe.com/test/apikeys
   - **Publishable key** (for frontend): `pk_test_51QX...`
   - **Secret key** (for backend): `sk_test_51QX...`

### 2. Update `application.yml`

```yaml
stripe:
  api-key: sk_test_YOUR_SECRET_KEY_HERE
  webhook-secret: whsec_YOUR_WEBHOOK_SECRET_HERE  # We'll get this later
  currency: EUR
```

### 3. Start the Application

```bash
mvn spring-boot:run
```

You should see in logs:
```
✓ Stripe SDK initialized with API key: sk_test_51QX...xyz
✓ Default currency: EUR
ℹ️  TEST mode: Using Stripe test API key
```

---

## 🧪 Test Scenarios

### **Scenario 1: Successful Payment** ✅

#### Step 1: Create a Reservation

```bash
POST http://localhost:8082/api/reservation
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "userId": 1,
  "filmProjectionId": 1,
  "termsAccepted": true,
  "marketingConsent": false,
  "tickets": [
    {
      "seatId": 10,
      "priceTypeId": 1,
      "basePrice": 12.50
    }
  ]
}
```

**Response:**
```json
{
  "id": 789,
  "referenceNumber": "RES-2024-001234",
  "status": "PENDING",
  "totalAmount": 12.50,
  "expirationDate": "2024-12-21T18:45:00"
}
```

#### Step 2: Create Payment Intent

```bash
POST http://localhost:8082/api/payment/create-payment-intent
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "reservationId": 789
}
```

**Response:**
```json
{
  "clientSecret": "pi_3QXxxxxxxxxxxxx_secret_yyyyyyyy",
  "amount": 12.50,
  "currency": "EUR",
  "paymentIntentId": "pi_3QXxxxxxxxxxxxx"
}
```

#### Step 3: Test Payment with Stripe CLI (Simulating Frontend)

**Install Stripe CLI**: https://stripe.com/docs/stripe-cli

```bash
# Login to Stripe CLI
stripe login

# Create a test payment
stripe payment_intents confirm pi_3QXxxxxxxxxxxxx \
  --payment-method=pm_card_visa
```

**Test Cards:**

| Card Number         | Scenario                    |
|--------------------|-----------------------------|
| `4242 4242 4242 4242` | ✅ Success                  |
| `4000 0000 0000 9995` | ❌ Declined                 |
| `4000 0025 0000 3155` | 🔐 Requires 3D Secure      |
| `4000 0000 0000 0002` | ❌ Card declined            |
| `4000 0000 0000 9987` | ❌ Insufficient funds       |

**Expiry**: Any future date (e.g., `12/25`)
**CVV**: Any 3 digits (e.g., `123`)

---

### **Scenario 2: Failed Payment** ❌

Use declined card:

```bash
stripe payment_intents confirm pi_3QXxxxxxxxxxxxx \
  --payment-method-type=card \
  --payment-method-data[type]=card \
  --payment-method-data[card][number]=4000000000009995
```

**Expected:**
- Payment status: `FAILED`
- Reservation remains: `PENDING`
- User can retry payment

---

### **Scenario 3: Webhook Testing** 🔔

#### Option A: Stripe CLI (Recommended for Local Testing)

```bash
# Forward webhooks to your local server
stripe listen --forward-to http://localhost:8082/api/payment/webhook

# This will output your webhook secret:
# > Ready! Your webhook signing secret is whsec_XXXXX
```

**Update `application.yml` with the webhook secret:**

```yaml
stripe:
  webhook-secret: whsec_XXXXX  # From Stripe CLI
```

**Trigger a test webhook:**

```bash
stripe trigger payment_intent.succeeded
```

**Expected Logs:**

```
📩 Received Stripe webhook
✓ Webhook signature verified
Processing webhook event: payment_intent.succeeded
✅ Payment succeeded for PaymentIntent: pi_XXX (Reservation: 789)
✓ Payment confirmed successfully
✓ Reservation 789 confirmed
```

#### Option B: Ngrok (For Testing with Real Stripe Dashboard)

```bash
# Install ngrok: https://ngrok.com/download
# Start ngrok tunnel
ngrok http 8082

# Example output:
# Forwarding: https://abc123.ngrok.io -> http://localhost:8082
```

**Configure webhook in Stripe Dashboard:**

1. Go to: https://dashboard.stripe.com/test/webhooks
2. Click **+ Add endpoint**
3. Endpoint URL: `https://abc123.ngrok.io/api/payment/webhook`
4. Select events:
   - `payment_intent.succeeded`
   - `payment_intent.payment_failed`
   - `payment_intent.canceled`
5. Add endpoint
6. Copy **Signing secret**: `whsec_XXXXX`
7. Update `application.yml`

---

## 📊 Verify Payment in Stripe Dashboard

1. Go to: https://dashboard.stripe.com/test/payments
2. Find your payment by:
   - Amount: €12.50
   - Metadata: `reservationId`, `referenceNumber`
3. Check payment status
4. View webhook deliveries

---

## 🔍 Check Database

```sql
-- Check payment transaction
SELECT * FROM payment_transaction 
WHERE reservation_id = 789;

-- Expected:
-- status: 'SUCCESS'
-- paid_at: (timestamp)
-- stripe_payment_intent_id: 'pi_3QX...'

-- Check reservation status
SELECT id, reference_number, status, confirmation_date 
FROM reservation 
WHERE id = 789;

-- Expected:
-- status: 'CONFIRMED'
-- confirmation_date: (timestamp)
```

---

## 🐛 Troubleshooting

### **Issue 1: "Invalid API Key"**

**Symptoms:**
```
❌ Stripe API error: Invalid API Key provided
```

**Solution:**
- Check `application.yml` has correct `stripe.api-key`
- Ensure key starts with `sk_test_` (not `pk_test_`)
- Restart application after changing config

---

### **Issue 2: "Invalid Webhook Signature"**

**Symptoms:**
```
❌ Invalid webhook signature! Possible security threat.
```

**Solution:**
- Ensure `stripe.webhook-secret` matches Stripe CLI or Dashboard
- Use `stripe listen` for local testing
- Restart application after updating secret

---

### **Issue 3: "Terms and conditions must be accepted"**

**Symptoms:**
```
IllegalStateException: Terms and conditions must be accepted before payment
```

**Solution:**
- Set `termsAccepted: true` in reservation creation request

---

### **Issue 4: Webhook Not Received**

**Symptoms:**
- Payment succeeds in Stripe, but reservation not confirmed

**Solution:**
- Use `stripe listen --forward-to http://localhost:8082/api/payment/webhook`
- Check logs for webhook receipt
- Verify `/payment/webhook` is in `SecurityConfig.permitAll()`

---

## 🎯 End-to-End Test Checklist

- [ ] Application starts without errors
- [ ] Stripe SDK initialized with test key
- [ ] Create reservation with `termsAccepted: true`
- [ ] Create payment intent (get `clientSecret`)
- [ ] Confirm payment with test card `4242 4242 4242 4242`
- [ ] Webhook received and processed
- [ ] Payment transaction status: `SUCCESS`
- [ ] Reservation status: `CONFIRMED`
- [ ] Payment visible in Stripe Dashboard
- [ ] Database records updated correctly

---

## 📚 Next Steps

1. **Frontend Integration**:
   - Install Stripe.js: `npm install @stripe/stripe-js`
   - Create payment form with Stripe Elements
   - Handle 3D Secure flow

2. **Email Notifications**:
   - Generate QR codes for tickets
   - Send confirmation email with PDF

3. **Error Handling**:
   - User-friendly error messages
   - Retry logic for failed payments

4. **Production Deployment**:
   - Replace test keys with live keys
   - Set up production webhooks
   - Enable webhook signature verification
   - Add monitoring and logging

---

## 🔗 Useful Links

- **Stripe Dashboard (Test)**: https://dashboard.stripe.com/test/dashboard
- **Stripe API Docs**: https://stripe.com/docs/api
- **Stripe Java SDK**: https://github.com/stripe/stripe-java
- **Test Cards**: https://stripe.com/docs/testing
- **Webhooks Guide**: https://stripe.com/docs/webhooks
- **Stripe CLI**: https://stripe.com/docs/stripe-cli

---

**🎉 Happy Testing!**









