-- ============================================
-- Migration V009: Create TICKET table
-- ============================================

-- Drop old ticket table
DROP TABLE IF EXISTS ticket CASCADE;

-- Create new ticket table
CREATE TABLE IF NOT EXISTS ticket (
    id BIGSERIAL PRIMARY KEY,
    
    -- Relations
    reservation_id BIGINT NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
    place_id BIGINT NOT NULL REFERENCES place(id),
    price_type_id BIGINT NOT NULL REFERENCES price_type(id),
    
    -- Prices (snapshot at reservation time)
    base_price DECIMAL(10,2) NOT NULL,
    final_price DECIMAL(10,2) NOT NULL,
    
    -- QR code for entry validation
    qr_code VARCHAR(500) UNIQUE,
    qr_code_generated_at TIMESTAMP,
    
    -- Usage tracking
    is_used BOOLEAN DEFAULT false NOT NULL,
    used_at TIMESTAMP,
    validated_by VARCHAR(100),
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_ticket_reservation ON ticket(reservation_id);
CREATE INDEX idx_ticket_place ON ticket(place_id);
CREATE INDEX idx_ticket_price_type ON ticket(price_type_id);
CREATE INDEX idx_ticket_qr_code ON ticket(qr_code);
CREATE INDEX idx_ticket_is_used ON ticket(is_used);

-- Constraint: One seat can only be booked once per reservation
CREATE UNIQUE INDEX idx_unique_place_per_reservation 
    ON ticket(place_id, reservation_id);

-- Function to auto-update updated_at
CREATE OR REPLACE FUNCTION update_ticket_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for updated_at
CREATE TRIGGER trigger_update_ticket_updated_at
    BEFORE UPDATE ON ticket
    FOR EACH ROW
    EXECUTE FUNCTION update_ticket_updated_at();

-- Add table and column comments
COMMENT ON TABLE ticket IS 'Individual tickets linked to a reservation';
COMMENT ON COLUMN ticket.qr_code IS 'Unique QR code for entry validation';
COMMENT ON COLUMN ticket.final_price IS 'Price after applying price type discount';
COMMENT ON COLUMN ticket.is_used IS 'Ticket has been validated at entry';
COMMENT ON COLUMN ticket.validated_by IS 'Staff member who validated the ticket';

-- Success message
DO $$
BEGIN
    RAISE NOTICE '✓ Migration V009 completed: TICKET table created';
END $$;

