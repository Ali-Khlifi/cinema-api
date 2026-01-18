# 🚀 Progressive Migration Plan - Cinema Database

> **Strategy:** Incremental changes without breaking existing system  
> **Approach:** Test each phase independently before moving to next  
> **Rollback:** Each migration can be reverted if needed

---

## 🎯 Migration Principles

### ✅ DO
- Create new tables alongside existing ones
- Test thoroughly before modifying existing tables
- Keep backups of original tables
- Add new columns with NULL/DEFAULT values first
- Migrate data progressively
- Use transactions where possible

### ❌ DON'T
- Drop existing tables until fully migrated
- Modify existing columns directly
- Change primary keys of active tables
- Delete data without backup

---

## 📅 Phase-by-Phase Plan

---

## 🟢 PHASE 1: Foundation Tables (Safe - No Impact)

**Goal:** Create new tables that don't touch existing system

### Migrations

#### V006: Create `user` table
```sql
-- New table for user management
-- Synced with Keycloak
-- NO IMPACT on existing tables
```

**Dependencies:** None  
**Risk:** 🟢 LOW - Completely new table  
**Rollback:** Simply drop table  

#### V007: Create `price_type` table
```sql
-- New table for pricing tiers
-- Includes initial data (NORMAL, REDUCED, CHILD, SENIOR)
-- NO IMPACT on existing tables
```

**Dependencies:** None  
**Risk:** 🟢 LOW - Completely new table  
**Rollback:** Simply drop table  

### Testing Checklist
- [ ] Tables created successfully
- [ ] Constraints work (UNIQUE, CHECK)
- [ ] Initial data inserted for `price_type`
- [ ] Existing application still runs
- [ ] Existing endpoints still work

### Success Criteria
✅ New tables created  
✅ No errors in logs  
✅ Existing system unchanged  

**Duration:** 1 day  
**Status:** Ready to implement

---

## 🟡 PHASE 2: New Reservation Model (Medium Impact)

**Goal:** Create new reservation structure alongside old one

### Migrations

#### V008: Backup existing `ticket` table
```sql
-- Rename old table as backup
ALTER TABLE ticket RENAME TO ticket_old_backup;

-- Keep it for data migration later
```

**Risk:** 🟡 MEDIUM - Existing ticket table renamed  
**Mitigation:** Backup available, can be restored  

#### V009: Create `reservation` table
```sql
-- New reservation model
-- FK to user (Phase 1)
-- FK to film_projection (existing)
-- Triggers for auto-generation of reference_number
```

**Dependencies:** V006 (user table must exist)  
**Risk:** 🟢 LOW - New table  

#### V010: Create new `ticket` table
```sql
-- Refactored ticket model
-- FK to reservation (Phase 2)
-- FK to seat (existing)
-- FK to price_type (Phase 1)
-- Includes QR code field
```

**Dependencies:** V007 (price_type), V009 (reservation)  
**Risk:** 🟢 LOW - New table  

### Data Migration Strategy

**Option A: Manual Testing (Recommended for Phase 2)**
```sql
-- Don't migrate old data yet
-- Create new reservations from scratch
-- Test the new flow with fresh data
-- Keep ticket_old_backup for reference
```

**Option B: Automated Migration (Later)**
```sql
-- Once validated, migrate old tickets
-- Create "guest" user for old reservations
-- Map old tickets → new reservations
```

### Testing Checklist
- [ ] Reservation table created
- [ ] Ticket table created
- [ ] Can create a manual reservation
- [ ] Can create tickets linked to reservation
- [ ] Reference number auto-generated
- [ ] Old backup table still accessible
- [ ] Application starts without errors

### Success Criteria
✅ New tables functional  
✅ Can create test reservation  
✅ Old data preserved in backup  
✅ No impact on existing endpoints (old ones still work)  

**Duration:** 2-3 days  
**Status:** Pending Phase 1 completion

---

## 🟡 PHASE 3: Cinema Address Refactoring (Medium Impact)

**Goal:** Update existing cinema structure for detailed address

### Migrations

#### V011: Update `city` table
```sql
-- Remove: longitude, latitude, altitude
ALTER TABLE city 
    DROP COLUMN IF EXISTS longitude,
    DROP COLUMN IF EXISTS latitude,
    DROP COLUMN IF EXISTS altitude;

-- Add audit columns if missing
ALTER TABLE city
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();
```

**Risk:** 🟡 MEDIUM - Modifies existing table  
**Mitigation:** Columns are NOT NULL with defaults  
**Impact:** Existing cinemas continue to work  

#### V012: Update `cinema` table
```sql
-- Add new address fields (with NULL allowed initially)
ALTER TABLE cinema
    ADD COLUMN street_number VARCHAR(10),
    ADD COLUMN street_name VARCHAR(255),
    ADD COLUMN postal_code VARCHAR(10);

-- Keep old 'adresse' field temporarily
-- Rename: adresse → address_old (for migration)
ALTER TABLE cinema RENAME COLUMN adresse TO address_old;

-- Add audit columns
ALTER TABLE cinema
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();
```

**Risk:** 🟡 MEDIUM - Existing column renamed  
**Mitigation:** Data preserved in `address_old`  

### Data Migration Script

```sql
-- Parse existing addresses and populate new fields
-- Example: "80, quai Charles de Gaulle 69006 LYON"
-- → street_number: "80"
-- → street_name: "quai Charles de Gaulle"
-- → postal_code: "69006"

UPDATE cinema
SET 
    street_number = /* extract from address_old */,
    street_name = /* extract from address_old */,
    postal_code = /* extract from address_old */
WHERE address_old IS NOT NULL;

-- After validation, drop old column
-- ALTER TABLE cinema DROP COLUMN address_old;
```

### Testing Checklist
- [ ] City table updated (no coordinates)
- [ ] Cinema table has new address fields
- [ ] Existing cinema data preserved
- [ ] Data migration script runs successfully
- [ ] Cinema endpoints still work
- [ ] Frontend can display new address format

### Success Criteria
✅ Address fields separated  
✅ Existing data migrated  
✅ Old address preserved as backup  
✅ Endpoints updated to return new format  

**Duration:** 2 days  
**Status:** Pending Phase 2 completion

---

## 🟡 PHASE 4: Projection Enhancements (Medium Impact)

**Goal:** Add projection limits and improve seat management

### Migrations

#### V013: Update `seat` table
```sql
-- Add row and seat_number (better structure)
ALTER TABLE seat
    ADD COLUMN row VARCHAR(5),
    ADD COLUMN seat_number INTEGER,
    ADD COLUMN seat_type VARCHAR(20) DEFAULT 'STANDARD',
    ADD COLUMN is_available BOOLEAN DEFAULT true;

-- Migrate existing 'numero' to row + seat_number
UPDATE seat
SET 
    row = CHR(65 + ((numero - 1) / 20)),        -- A, B, C, etc.
    seat_number = ((numero - 1) % 20) + 1;      -- 1-20 per row

-- Add unique constraint
CREATE UNIQUE INDEX idx_unique_seat_per_room 
    ON seat(room_id, row, seat_number);

-- Keep 'numero' for now, can drop later
```

**Risk:** 🟡 MEDIUM - Existing table modified  
**Mitigation:** Old 'numero' field preserved  

#### V014: Update `film_projection` table
```sql
-- Add max_projections limit
ALTER TABLE film_projection
    ADD COLUMN max_projections INTEGER DEFAULT NULL,
    ADD COLUMN end_time TIME,
    ADD COLUMN status VARCHAR(20) DEFAULT 'SCHEDULED';

-- Calculate end_time from existing data
UPDATE film_projection fp
SET end_time = (
    SELECT 
        fp.heure_debut::time + (f.duree || ' minutes')::interval
    FROM film f
    WHERE f.id = fp.film_id
)::time;

-- Add constraint on status
ALTER TABLE film_projection
    ADD CONSTRAINT chk_projection_status
    CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'));

-- Update unique constraint to include end_time
DROP INDEX IF EXISTS /* existing unique constraint */;
CREATE UNIQUE INDEX idx_unique_projection
    ON film_projection(film_id, room_id, projection_date, start_time, end_time);
```

**Risk:** 🟡 MEDIUM - Unique constraint changed  
**Mitigation:** New constraint more accurate  

### Testing Checklist
- [ ] Seat table updated with row/number
- [ ] Existing seats migrated correctly
- [ ] Film_projection has max_projections
- [ ] end_time calculated for existing projections
- [ ] Status constraint works
- [ ] New unique constraint enforced
- [ ] Reservation checks max_projections

### Success Criteria
✅ Seats have row/number structure  
✅ Projections have limits  
✅ System blocks reservation when max reached  
✅ Existing data migrated  

**Duration:** 2-3 days  
**Status:** Pending Phase 3 completion

---

## 🟢 PHASE 5: Payment & Notifications (Safe - New Tables)

**Goal:** Add payment processing and email notification tracking

### Migrations

#### V015: Create `payment` table
```sql
-- Payment transactions
-- FK to reservation (Phase 2)
-- FK to user (Phase 1)
-- Stripe integration fields
```

**Dependencies:** V009 (reservation), V006 (user)  
**Risk:** 🟢 LOW - New table  

#### V016: Create `email_notification` table
```sql
-- Email history
-- FK to user (Phase 1)
-- FK to reservation (Phase 2)
-- Retry mechanism
```

**Dependencies:** V009 (reservation), V006 (user)  
**Risk:** 🟢 LOW - New table  

### Testing Checklist
- [ ] Payment table created
- [ ] Email_notification table created
- [ ] Can create payment record
- [ ] Can track email status
- [ ] Stripe sandbox integration works
- [ ] Email service works (or logs correctly)

### Success Criteria
✅ Payment tracking functional  
✅ Email notifications tracked  
✅ Full reservation flow works end-to-end  

**Duration:** 2-3 days  
**Status:** Pending Phase 4 completion

---

## 📊 Complete Timeline

| Phase | Duration | Dependencies | Risk | Status |
|-------|----------|--------------|------|--------|
| Phase 1 | 1 day | None | 🟢 LOW | Ready |
| Phase 2 | 2-3 days | Phase 1 | 🟡 MEDIUM | Pending |
| Phase 3 | 2 days | Phase 2 | 🟡 MEDIUM | Pending |
| Phase 4 | 2-3 days | Phase 3 | 🟡 MEDIUM | Pending |
| Phase 5 | 2-3 days | Phase 4 | 🟢 LOW | Pending |
| **TOTAL** | **10-13 days** | Sequential | | |

---

## 🔄 Rollback Procedures

### Phase 1 Rollback
```sql
DROP TABLE IF EXISTS user CASCADE;
DROP TABLE IF EXISTS price_type CASCADE;
```

### Phase 2 Rollback
```sql
-- Restore old ticket table
ALTER TABLE ticket_old_backup RENAME TO ticket;

-- Drop new tables
DROP TABLE IF EXISTS ticket CASCADE;
DROP TABLE IF EXISTS reservation CASCADE;
```

### Phase 3 Rollback
```sql
-- Restore old address column
ALTER TABLE cinema RENAME COLUMN address_old TO adresse;
ALTER TABLE cinema 
    DROP COLUMN street_number,
    DROP COLUMN street_name,
    DROP COLUMN postal_code;

-- Restore city coordinates (if backed up)
```

### Phase 4 Rollback
```sql
-- Remove new columns
ALTER TABLE seat DROP COLUMN row, DROP COLUMN seat_number;
ALTER TABLE film_projection 
    DROP COLUMN max_projections,
    DROP COLUMN end_time,
    DROP COLUMN status;

-- Restore old constraints
```

### Phase 5 Rollback
```sql
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS email_notification CASCADE;
```

---

## 🎯 Current Status & Next Action

### ✅ Completed
- [x] MCD validated with all corrections
- [x] Progressive migration plan created
- [x] Old migrations deleted

### 🚀 Next Immediate Step

**START PHASE 1:**
1. Create V006: `user` table migration
2. Create V007: `price_type` table migration
3. Test both migrations
4. Validate existing system still works

**Ready to proceed with Phase 1?** ✅

---

## 📝 Notes

- Each phase is independent
- Can pause between phases to validate
- Production deployment: one phase per week
- Development: can go faster if tests pass
- Always backup before running migrations
- Test rollback procedures in dev environment first

---

**Migration Strategy:** Conservative and safe ✅  
**Total Duration:** 2-3 weeks at steady pace  
**Risk Mitigation:** Backups and rollback scripts ready  













