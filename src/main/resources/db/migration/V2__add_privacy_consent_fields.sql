-- ═══════════════════════════════════════════════════════════════════════
-- Migración V2: Agregar campos de consentimiento de privacidad
-- Ley 1581 de 2012 - Protección de Datos Personales Colombia
-- ═══════════════════════════════════════════════════════════════════════

-- Agregar columnas
ALTER TABLE leads
ADD COLUMN privacy_policy_accepted BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE leads
ADD COLUMN privacy_policy_accepted_at TIMESTAMP;

ALTER TABLE leads
ADD COLUMN privacy_policy_version VARCHAR(10);

-- Crear índice para consultas de auditoría
CREATE INDEX idx_lead_privacy_accepted ON leads(privacy_policy_accepted);

-- Actualizar leads existentes (si los hay) con valores por defecto
-- IMPORTANTE: En producción, estos leads antiguos NO tienen consentimiento válido
UPDATE leads
SET
  privacy_policy_accepted = false,
  privacy_policy_version = 'PRE-1.0'
WHERE privacy_policy_accepted_at IS NULL;

-- Comentarios para documentación
COMMENT ON COLUMN leads.privacy_policy_accepted IS 'Consentimiento expreso del titular - Ley 1581/2012';
COMMENT ON COLUMN leads.privacy_policy_accepted_at IS 'Fecha/hora de aceptación de la política';
COMMENT ON COLUMN leads.privacy_policy_version IS 'Versión de la política aceptada (ej: 1.0)';