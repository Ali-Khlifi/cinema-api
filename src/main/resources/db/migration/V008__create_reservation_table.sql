-- ============================================
-- Migration V008: Create RESERVATION table
-- ============================================

-- Create reservation table
CREATE TABLE IF NOT EXISTS reservation (
    id BIGSERIAL PRIMARY KEY,
    
    -- Unique reference number (auto-generated)
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    
    -- Relations
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    film_projection_id BIGINT NOT NULL REFERENCES film_projection(id),
    
    -- Status: PENDING, CONFIRMED, PAID, CANCELLED, EXPIRED
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    -- Amounts
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    
    -- Promo code (optional)
    promo_code VARCHAR(50),
    
    -- Important dates
    reservation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP,
    confirmation_date TIMESTAMP,
    cancellation_date TIMESTAMP,
    cancellation_reason TEXT,
    
    -- Audit columns (Auditable interface)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- Create indexes for performance
CREATE INDEX idx_reservation_user ON reservation(user_id);
CREATE INDEX idx_reservation_projection ON reservation(film_projection_id);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_reservation_reference ON reservation(reference_number);
CREATE INDEX idx_reservation_date ON reservation(reservation_date);
CREATE INDEX idx_reservation_deleted ON reservation(deleted_at) WHERE deleted_at IS NULL;

-- Add status constraint
ALTER TABLE reservation 
    ADD CONSTRAINT chk_reservation_status 
    CHECK (status IN ('PENDING', 'CONFIRMED', 'PAID', 'CANCELLED', 'EXPIRED'));

-- Create sequence for reference number generation
CREATE SEQUENCE IF NOT EXISTS reservation_reference_seq START WITH 1;

-- Function to auto-generate reference number
CREATE OR REPLACE FUNCTION generate_reservation_reference()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.reference_number IS NULL OR NEW.reference_number = '' THEN
        NEW.reference_number := 'RES-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || 
                               LPAD(nextval('reservation_reference_seq')::TEXT, 6, '0');
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to auto-generate reference number
CREATE TRIGGER trigger_generate_reservation_reference
    BEFORE INSERT ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION generate_reservation_reference();

-- Function to auto-update updated_at
CREATE OR REPLACE FUNCTION update_reservation_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for updated_at
CREATE TRIGGER trigger_update_reservation_updated_at
    BEFORE UPDATE ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION update_reservation_updated_at();

-- Add table and column comments
COMMENT ON TABLE reservation IS 'Customer reservations grouping multiple tickets';
COMMENT ON COLUMN reservation.reference_number IS 'Unique reference (e.g., RES-2025-000123)';
COMMENT ON COLUMN reservation.status IS 'PENDING: awaiting payment, CONFIRMED: confirmed, PAID: paid, CANCELLED: cancelled, EXPIRED: expired';
COMMENT ON COLUMN reservation.expiration_date IS 'Reservation expires if not paid within 15 minutes';
COMMENT ON COLUMN reservation.deleted_at IS 'Soft delete timestamp (NULL = not deleted)';

-- Success message
DO $$
BEGIN
    RAISE NOTICE '✓ Migration V008 completed: RESERVATION table created';
END $$;

