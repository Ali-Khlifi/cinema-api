-- ============================================
-- Migration V007: Create PRICE_TYPE table
-- Purpose: Pricing tiers for tickets (Normal, Reduced, Child, Senior)
-- Author: Cinema Team
-- Date: 2025-12-14
-- Risk: LOW (new table, no impact on existing)
-- ============================================

-- Create price_type table
CREATE TABLE IF NOT EXISTS price_type (
    id BIGSERIAL PRIMARY KEY,
    
    -- Identification
    code VARCHAR(20) UNIQUE NOT NULL,
    label VARCHAR(100) NOT NULL,
    description TEXT,
    
    -- Discount percentage
    discount_percentage DECIMAL(5,2) DEFAULT 0.00 NOT NULL,
    
    -- Status
    is_active BOOLEAN DEFAULT true NOT NULL,
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_price_type_code ON price_type(code);
CREATE INDEX idx_price_type_is_active ON price_type(is_active);

-- Add constraint: discount percentage must be between 0 and 100
ALTER TABLE price_type 
    ADD CONSTRAINT chk_discount_percentage 
    CHECK (discount_percentage >= 0 AND discount_percentage <= 100);

-- Function to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_price_type_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update updated_at
CREATE TRIGGER trigger_update_price_type_updated_at
    BEFORE UPDATE ON price_type
    FOR EACH ROW
    EXECUTE FUNCTION update_price_type_updated_at();

-- Insert initial pricing tiers
INSERT INTO price_type (code, label, description, discount_percentage, is_active) VALUES
('NORMAL', 'Normal Price', 'Full price ticket without discount', 0.00, true),
('REDUCED', 'Reduced Price', 'For students and job seekers (valid ID required)', 25.00, true),
('CHILD', 'Child Price', 'For children under 12 years old', 40.00, true),
('SENIOR', 'Senior Price', 'For seniors over 60 years old', 30.00, true)
ON CONFLICT (code) DO NOTHING;

-- Add table and column comments
COMMENT ON TABLE price_type IS 'Pricing tiers available for ticket purchase';
COMMENT ON COLUMN price_type.code IS 'Unique code identifier (e.g., NORMAL, REDUCED)';
COMMENT ON COLUMN price_type.label IS 'Human-readable label for the price type';
COMMENT ON COLUMN price_type.discount_percentage IS 'Percentage discount applied to base price (0-100)';
COMMENT ON COLUMN price_type.is_active IS 'Whether this price type is currently available';

-- Verify data insertion
DO $$
DECLARE
    row_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO row_count FROM price_type;
    RAISE NOTICE '✓ Migration V007 completed: PRICE_TYPE table created with % initial records', row_count;
END $$;

