-- =============================================================================
-- Migration: V015 - Create PAYMENT_TRANSACTION table for Stripe traceability
-- Description: Minimal table to track Stripe payments
-- Author: Cinema API Team
-- Date: 2024-12-21
-- =============================================================================

-- Step 1: Create payment_transaction table
CREATE TABLE payment_transaction (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    stripe_payment_intent_id VARCHAR(255) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED', 'CANCELLED')),
    stripe_payment_method VARCHAR(50),
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Foreign key constraint
    CONSTRAINT fk_payment_reservation 
        FOREIGN KEY (reservation_id) 
        REFERENCES reservation(id) 
        ON DELETE CASCADE
);

-- Step 2: Create indexes for performance
CREATE INDEX idx_payment_reservation ON payment_transaction(reservation_id);
CREATE INDEX idx_payment_stripe_id ON payment_transaction(stripe_payment_intent_id);
CREATE INDEX idx_payment_status ON payment_transaction(status);
CREATE INDEX idx_payment_created_at ON payment_transaction(created_at DESC);

-- Step 3: Create trigger for updated_at
CREATE OR REPLACE FUNCTION update_payment_transaction_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_payment_transaction_updated_at
BEFORE UPDATE ON payment_transaction
FOR EACH ROW
EXECUTE FUNCTION update_payment_transaction_updated_at();

-- Step 4: Add helpful comments
COMMENT ON TABLE payment_transaction IS 'Tracks Stripe payment transactions for reservations (minimal for traceability)';
COMMENT ON COLUMN payment_transaction.stripe_payment_intent_id IS 'Stripe PaymentIntent ID (unique identifier from Stripe API)';
COMMENT ON COLUMN payment_transaction.amount IS 'Payment amount in EUR (matches reservation total_amount)';
COMMENT ON COLUMN payment_transaction.currency IS 'Payment currency (default: EUR)';
COMMENT ON COLUMN payment_transaction.status IS 'Payment status: PENDING, SUCCESS, FAILED, REFUNDED, CANCELLED';
COMMENT ON COLUMN payment_transaction.stripe_payment_method IS 'Payment method type from Stripe (card, paypal, etc.)';
COMMENT ON COLUMN payment_transaction.paid_at IS 'Timestamp when payment was successfully completed';

-- Step 5: Success message
DO $$
BEGIN
    RAISE NOTICE '✓ Migration V015 completed: PAYMENT_TRANSACTION table created with indexes and triggers';
END $$;









