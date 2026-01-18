# 🎬 Cinema Management System - VALIDATED Conceptual Data Model (MCD)

> **Status:** ✅ VALIDATED - Ready for implementation  
> **Last Updated:** December 14, 2025  
> **Language:** English (code & database)

---

## 🎯 Key Decisions & Requirements

### ✅ Validated Business Rules

1. **No Guest Cart** - Non-authenticated users can only browse films. Login required before any reservation.
2. **Seance merged into FilmProjection** - Single table with start_time and end_time.
3. **User identified by keycloak_id** - user_id is the table PK, keycloak_id is the Keycloak UUID.
4. **Auditable entities** - Generic interface for entities with soft delete (deleted_at).
5. **Projection limits** - FilmProjection has max_projections per film to control availability.
6. **Unique projections** - Identified by (film_id, room_id, projection_date, start_time, end_time).
7. **Detailed cinema address** - Separate fields: city, postal_code, street_number, street_name.
8. **City simplified** - No coordinates (longitude, latitude, altitude).
9. **Progressive migration** - Step-by-step to preserve existing working system.

---

## 📊 Simplified Entity List

### Core Entities (14 total - NO GuestCart)

1. **City** - Geographic location
2. **Cinema** - Physical cinema with detailed address
3. **Room** - Screening room
4. **Seat** - Individual seat with row/number
5. **Category** - Film genre
6. **Film** - Movie information
7. **FilmProjection** - Scheduled screening (merged with Seance)
8. **PriceType** - Pricing tiers (Normal, Reduced, Child, Senior)
9. **User** - Registered user (Keycloak synced)
10. **Reservation** - Booking (requires authentication)
11. **Ticket** - Individual ticket (seat + price)
12. **Payment** - Payment transaction
13. **EmailNotification** - Email history

---

## 🔗 Entity-Relationship Diagram (Simplified)

```
┌──────────┐
│   CITY   │
│  PK: id  │
└────┬─────┘
     │ 1
     │
     │ N
┌────▼──────────────┐         ┌───────────┐
│     CINEMA        │         │ CATEGORY  │
│   PK: id          │         │  PK: id   │
│   FK: city_id     │         └─────┬─────┘
│   + detailed addr │               │ 1
└────┬──────────────┘               │
     │ 1                            │ N
     │                        ┌─────▼────┐
     │ N                      │   FILM   │
┌────▼─────┐                  │  PK: id  │
│   ROOM   │                  │ FK: cat. │
│  PK: id  │                  └─────┬────┘
│ FK: cinema│                       │ 1
└────┬─────┘                        │
     │ 1                            │ N
     ├─────────────┬────────────────▼──────────────┐
     │ N           │                                │
┌────▼─────┐      │    ┌───────────────────────┐   │
│   SEAT   │      │    │  FILM_PROJECTION      │   │
│  PK: id  │      │    │  PK: id               │   │
│ FK: room │      │    │  FK: film_id, room_id │   │
│ + row    │      │    │  + max_projections    │   │
│ + number │      │    │  + start/end time     │   │
└────┬─────┘      │    └──────────┬────────────┘   │
     │            │               │ 1              │
     │ N          │               │                │
     │            │               │ N              │
     │      ┌─────┴───────────────▼──────┐         │
     │      │                             │         │
     │      │       RESERVATION           │         │
     │      │       PK: id                │         │
     │      │       FK: user_id           │         │
     │      │       FK: projection_id     │         │
     │      │       reference_number      │         │
     │      │       status                │         │
     │      └────────────┬────────────────┘         │
     │                   │ 1                        │
     │                   │                          │
     │                   │ N                        │
     │            ┌──────▼──────────┐        ┌──────────────┐
     └────────────►     TICKET      │        │  PRICE_TYPE  │
                  │     PK: id      │◄───────│  PK: id      │
                  │  FK: reservation│        │  (Normal,    │
                  │  FK: seat_id    │        │   Reduced,   │
                  │  FK: price_type │        │   Child...)  │
                  │  + qr_code      │        └──────────────┘
                  └─────────────────┘


┌──────────────┐
│     USER     │
│   PK: id     │
│ UK: keycloak_│
│      id      │
│ UK: email    │
└──────┬───┬───┘
       │ 1 │ 1
       │   │
       │ N │ N
┌──────▼─┐ │   ┌───────────────────┐
│PAYMENT │ │   │ EMAIL_NOTIFICATION│
│ PK: id │ │   │     PK: id        │
│FK: res.│ └───┤  FK: user_id      │
│FK: user│     │  FK: reservation  │
│Stripe  │     │  + type/status    │
└────────┘     └───────────────────┘
```

---

## 📝 Detailed Entity Definitions

### 1. CITY (Simplified)

```sql
city
├── id (BIGSERIAL, PK)
├── name (VARCHAR(255), NOT NULL)
├── created_at (TIMESTAMP, NOT NULL DEFAULT NOW())
└── updated_at (TIMESTAMP, NOT NULL DEFAULT NOW())

-- Removed: longitude, latitude, altitude (not needed)
```

**Business Rules:**
- Simple geographic identifier
- No geo-coordinates needed

---

### 2. CINEMA (Detailed Address)

```sql
cinema
├── id (BIGSERIAL, PK)
├── city_id (BIGINT, FK → city.id)
├── name (VARCHAR(255), NOT NULL)
├── street_number (VARCHAR(10))          -- "80", "112", etc.
├── street_name (VARCHAR(255))           -- "Quai Charles de Gaulle"
├── postal_code (VARCHAR(10))            -- "69006"
├── total_rooms (INTEGER)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

**Changes from original:**
- ✅ `address` field replaced by separate fields
- ✅ `street_number`, `street_name`, `postal_code` added
- ✅ More structured, better for search/filtering

**Example:**
```
UGC Cité Internationale
80, Quai Charles de Gaulle
69006 LYON
```

---

### 3. ROOM

```sql
room
├── id (BIGSERIAL, PK)
├── cinema_id (BIGINT, FK → cinema.id, ON DELETE CASCADE)
├── name (VARCHAR(255), NOT NULL)
├── total_seats (INTEGER)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

---

### 4. SEAT

```sql
seat
├── id (BIGSERIAL, PK)
├── room_id (BIGINT, FK → room.id, ON DELETE CASCADE)
├── row (VARCHAR(5), NOT NULL)              -- A, B, C, etc.
├── seat_number (INTEGER, NOT NULL)          -- 1, 2, 3, etc.
├── seat_type (VARCHAR(20) DEFAULT 'STANDARD')  -- STANDARD, VIP, ACCESSIBLE
├── is_available (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
├── updated_at (TIMESTAMP, NOT NULL)
└── CONSTRAINT unique_seat_per_room UNIQUE(room_id, row, seat_number)
```

---

### 5. CATEGORY

```sql
category
├── id (BIGSERIAL, PK)
├── name (VARCHAR(75), NOT NULL)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

---

### 6. FILM

```sql
film
├── id (BIGSERIAL, PK)
├── category_id (BIGINT, FK → category.id)
├── title (VARCHAR(255), NOT NULL)
├── duration (DOUBLE PRECISION)              -- minutes
├── director (VARCHAR(255))
├── description (TEXT)
├── media (BYTEA)                            -- poster image
├── release_date (DATE)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

---

### 7. FILM_PROJECTION (Merged with Seance + Projection Limits)

```sql
film_projection
├── id (BIGSERIAL, PK)
├── film_id (BIGINT, FK → film.id)
├── room_id (BIGINT, FK → room.id)
├── projection_date (DATE, NOT NULL)
├── start_time (TIME, NOT NULL)
├── end_time (TIME, NOT NULL)                -- Explicitly stored
├── base_price (DECIMAL(10,2), NOT NULL)
├── max_projections (INTEGER)                -- ✅ NEW: Limit per film/date
├── status (VARCHAR(20) DEFAULT 'SCHEDULED') -- SCHEDULED, ONGOING, COMPLETED, CANCELLED
├── available_seats (INTEGER)
├── reserved_seats (INTEGER, DEFAULT 0)
├── total_seats (INTEGER)
├── is_full (BOOLEAN, DEFAULT false)
├── created_at (TIMESTAMP, NOT NULL)
├── updated_at (TIMESTAMP, NOT NULL)
└── CONSTRAINT unique_projection UNIQUE(film_id, room_id, projection_date, start_time, end_time)
```

**✅ NEW: max_projections Logic**

Example: "Need For Speed" film
- Admin sets: `max_projections = 10` for Monday
- System counts: How many projections exist for this film on Monday?
- If count >= 10 AND all are full → Block new reservations
- Frontend message: "All projections for this film are full"

**Business Rules:**
- Unique constraint includes `end_time` now
- `max_projections` controls how many screenings of a film are allowed
- System checks before allowing new reservation

---

### 8. PRICE_TYPE

```sql
price_type
├── id (BIGSERIAL, PK)
├── code (VARCHAR(20), UNIQUE, NOT NULL)     -- NORMAL, REDUCED, CHILD, SENIOR
├── label (VARCHAR(100), NOT NULL)
├── description (TEXT)
├── discount_percentage (DECIMAL(5,2))       -- 0.00 to 100.00
├── is_active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

**Initial Data:**
```sql
INSERT INTO price_type (code, label, description, discount_percentage) VALUES
('NORMAL', 'Normal Price', 'Full price ticket', 0.00),
('REDUCED', 'Reduced Price', 'Students, job seekers (ID required)', 25.00),
('CHILD', 'Child Price', 'Under 12 years old', 40.00),
('SENIOR', 'Senior Price', 'Over 60 years old', 30.00);
```

---

### 9. USER (Keycloak Synced)

```sql
user
├── id (BIGSERIAL, PK)                       -- ✅ user_id (not keycloak_id)
├── keycloak_id (VARCHAR(255), UNIQUE, NOT NULL)  -- UUID from Keycloak
├── email (VARCHAR(255), UNIQUE, NOT NULL)
├── first_name (VARCHAR(100))
├── last_name (VARCHAR(100))
├── phone (VARCHAR(20))
├── date_of_birth (DATE)
├── is_active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
├── updated_at (TIMESTAMP, NOT NULL)
└── deleted_at (TIMESTAMP, NULL)             -- ✅ Soft delete (Auditable)
```

**Changes:**
- ✅ PK is `id` (user_id)
- ✅ `keycloak_id` is a separate unique field
- ✅ Implements Auditable interface (soft delete)

---

### 10. RESERVATION (Requires Authentication)

```sql
reservation
├── id (BIGSERIAL, PK)
├── reference_number (VARCHAR(50), UNIQUE, NOT NULL)  -- RES-YYYY-XXXXXX
├── user_id (BIGINT, FK → user.id)                   -- ✅ Required (no guest)
├── film_projection_id (BIGINT, FK → film_projection.id)
├── status (VARCHAR(20), NOT NULL DEFAULT 'PENDING')  -- PENDING, CONFIRMED, PAID, CANCELLED, EXPIRED
├── subtotal (DECIMAL(10,2), NOT NULL)
├── tax_amount (DECIMAL(10,2), DEFAULT 0.00)
├── discount_amount (DECIMAL(10,2), DEFAULT 0.00)
├── total_amount (DECIMAL(10,2), NOT NULL)
├── promo_code (VARCHAR(50), NULL)
├── reservation_date (TIMESTAMP, NOT NULL DEFAULT NOW())
├── expiration_date (TIMESTAMP, NULL)                 -- reservation_date + 15 min
├── confirmation_date (TIMESTAMP, NULL)
├── cancellation_date (TIMESTAMP, NULL)
├── cancellation_reason (TEXT, NULL)
├── created_at (TIMESTAMP, NOT NULL)
├── updated_at (TIMESTAMP, NOT NULL)
└── deleted_at (TIMESTAMP, NULL)                      -- ✅ Soft delete (Auditable)
```

**✅ Implements Auditable Interface**

---

### 11. TICKET

```sql
ticket
├── id (BIGSERIAL, PK)
├── reservation_id (BIGINT, FK → reservation.id, ON DELETE CASCADE)
├── seat_id (BIGINT, FK → seat.id)
├── price_type_id (BIGINT, FK → price_type.id)
├── base_price (DECIMAL(10,2), NOT NULL)
├── final_price (DECIMAL(10,2), NOT NULL)            -- after discount
├── qr_code (VARCHAR(500), UNIQUE, NULL)
├── qr_code_generated_at (TIMESTAMP, NULL)
├── is_used (BOOLEAN, DEFAULT false)
├── used_at (TIMESTAMP, NULL)
├── validated_by (VARCHAR(100), NULL)                -- staff who scanned
├── created_at (TIMESTAMP, NOT NULL)
├── updated_at (TIMESTAMP, NOT NULL)
└── CONSTRAINT unique_seat_per_reservation UNIQUE(seat_id, reservation_id)
```

---

### 12. PAYMENT

```sql
payment
├── id (BIGSERIAL, PK)
├── reservation_id (BIGINT, FK → reservation.id)
├── user_id (BIGINT, FK → user.id)
├── amount (DECIMAL(10,2), NOT NULL)
├── currency (VARCHAR(3), DEFAULT 'EUR')
├── payment_method (VARCHAR(50), NOT NULL)           -- CREDIT_CARD, PAYPAL, etc.
├── status (VARCHAR(20), NOT NULL DEFAULT 'PENDING') -- PENDING, SUCCESS, FAILED, REFUNDED
├── transaction_id (VARCHAR(255), UNIQUE, NULL)
├── provider (VARCHAR(50), NULL)                     -- STRIPE, PAYPAL
├── provider_payment_intent_id (VARCHAR(255), NULL)
├── provider_charge_id (VARCHAR(255), NULL)
├── payment_date (TIMESTAMP, NULL)
├── payment_metadata (JSONB, NULL)
├── refund_date (TIMESTAMP, NULL)
├── refund_amount (DECIMAL(10,2), NULL)
├── refund_reason (TEXT, NULL)
├── refund_id (VARCHAR(255), NULL)
├── error_code (VARCHAR(50), NULL)
├── error_message (TEXT, NULL)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

---

### 13. EMAIL_NOTIFICATION

```sql
email_notification
├── id (BIGSERIAL, PK)
├── user_id (BIGINT, FK → user.id)
├── reservation_id (BIGINT, FK → reservation.id, NULL)
├── notification_type (VARCHAR(50), NOT NULL)
│   -- RESERVATION_CONFIRMATION, TICKET_PDF, PAYMENT_SUCCESS,
│   -- PAYMENT_FAILED, REMINDER, CANCELLATION
├── recipient_email (VARCHAR(255), NOT NULL)
├── recipient_name (VARCHAR(200), NULL)
├── subject (VARCHAR(500), NOT NULL)
├── body (TEXT, NULL)
├── template_name (VARCHAR(100), NULL)
├── status (VARCHAR(20), NOT NULL DEFAULT 'PENDING') -- PENDING, SENT, FAILED, BOUNCED
├── sent_at (TIMESTAMP, NULL)
├── delivery_status (TEXT, NULL)
├── error_message (TEXT, NULL)
├── provider (VARCHAR(50), NULL)                     -- SENDGRID, AWS_SES, SMTP
├── provider_message_id (VARCHAR(255), NULL)
├── has_attachment (BOOLEAN, DEFAULT false)
├── attachment_path (VARCHAR(500), NULL)
├── attachment_filename (VARCHAR(255), NULL)
├── opened_at (TIMESTAMP, NULL)
├── clicked_at (TIMESTAMP, NULL)
├── retry_count (INTEGER, DEFAULT 0)
├── max_retries (INTEGER, DEFAULT 3)
├── next_retry_at (TIMESTAMP, NULL)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

---

## 🎯 Auditable Interface Pattern

**Generic Interface for Soft Delete:**

```java
public interface Auditable {
    Long getId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getDeletedAt();
    
    void setDeletedAt(LocalDateTime deletedAt);
}
```

**Entities implementing Auditable:**
- ✅ User (deleted_at)
- ✅ Reservation (deleted_at)

**Entities NOT implementing Auditable:**
- Cinema, Room, Seat, Film, etc. (hard delete or no delete)

---

## 🔄 Simplified Reservation Flow (No Guest Cart)

```
1. BROWSING (Non-authenticated) ✅
   └─> User can view:
       - List of films
       - Film details
       - Available projections
       - Seat map (availability)
   └─> User CANNOT click "Reserve"

2. LOGIN REQUIRED ⚠️
   └─> User clicks "Reserve" for a projection
   └─> System checks: isAuthenticated()?
   └─> If NO → Redirect to Keycloak login
   └─> After authentication → Redirect back to projection page

3. SEAT SELECTION (Authenticated) ✅
   └─> User selects seats + price types
   └─> System checks:
       - Seats still available?
       - Projection not full?
       - max_projections not exceeded?
   └─> If OK → Create Reservation (status: PENDING)
   └─> Create Tickets (one per seat)
   └─> Reservation.expiration_date = NOW() + 15 min

4. PAYMENT ✅
   └─> Payment intent created (Stripe)
   └─> User enters credit card
   └─> Payment.status = PENDING
   └─> Stripe webhook: payment.succeeded
   └─> Payment.status = SUCCESS
   └─> Reservation.status = PAID
   └─> Tickets.qr_code generated

5. CONFIRMATION ✅
   └─> EmailNotification created
   └─> PDF generated with tickets + QR codes
   └─> Email sent with attachment
   └─> EmailNotification.status = SENT

6. ENTRY VALIDATION ✅
   └─> Staff scans Ticket.qr_code
   └─> Verify: projection date/time, seat, not used
   └─> Ticket.is_used = true
   └─> Ticket.used_at = NOW()
```

**Key Simplification:**
- ❌ No GuestCart tables
- ❌ No cart conversion logic
- ✅ Login BEFORE seat selection
- ✅ Much simpler flow

---

## 📊 Progressive Migration Plan

### Phase 1: Foundation (Week 1)
- [x] Validate MCD
- [ ] V006: Create `user` table
- [ ] V007: Create `price_type` table (with initial data)
- [ ] Test: Keycloak sync works

### Phase 2: Core Reservations (Week 2)
- [ ] V008: Rename existing `ticket` to `ticket_old_backup`
- [ ] V009: Create new `reservation` table
- [ ] V010: Create new `ticket` table (refactored)
- [ ] Test: Create a reservation manually

### Phase 3: Address Refactoring (Week 2-3)
- [ ] V011: Update `city` (remove coordinates)
- [ ] V012: Update `cinema` (detailed address)
- [ ] Migrate existing data
- [ ] Test: Existing cinemas still work

### Phase 4: Projection Limits (Week 3)
- [ ] V013: Update `seat` (row, seat_number)
- [ ] V014: Update `film_projection` (max_projections, end_time)
- [ ] Migrate existing data
- [ ] Test: Projection limits work

### Phase 5: Payment & Notifications (Week 3-4)
- [ ] V015: Create `payment` table
- [ ] V016: Create `email_notification` table
- [ ] Test: Full flow end-to-end

---

## ✅ Final Validation Checklist

- [x] No GuestCart (login required before reservation)
- [x] User PK is `id`, `keycloak_id` is separate field
- [x] Auditable interface for soft delete (User, Reservation)
- [x] Projection unique with end_time
- [x] max_projections field added
- [x] City simplified (no coordinates)
- [x] Cinema address detailed (separate fields)
- [x] All names in English
- [x] Progressive migration plan defined

---

## 🚀 Next Step

**Phase 1: Create User + PriceType migrations**

Ready to proceed? ✅













