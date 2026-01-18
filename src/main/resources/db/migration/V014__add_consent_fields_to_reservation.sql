-- =====================================================
-- Add Terms & Marketing Consent Fields to Reservation
-- =====================================================
-- Description: Add fields to track user consent for terms and marketing
-- Author: Cinema Management System
-- Date: 2025-12-21

-- Add terms_accepted column (required, defaults to false)
ALTER TABLE reservation
ADD COLUMN terms_accepted BOOLEAN NOT NULL DEFAULT FALSE;

-- Add marketing_consent column (optional, defaults to false)
ALTER TABLE reservation
ADD COLUMN marketing_consent BOOLEAN DEFAULT FALSE;

-- Add comment for documentation
COMMENT ON COLUMN reservation.terms_accepted IS 'Indicates if user accepted terms and conditions for this reservation (required for payment)';
COMMENT ON COLUMN reservation.marketing_consent IS 'Indicates if user consents to receive marketing communications';









