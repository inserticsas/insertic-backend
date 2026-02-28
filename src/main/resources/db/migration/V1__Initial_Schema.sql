-- ══════════════════════════════════════════════════════════════════════════════
-- MIGRATION: V1__Initial_Schema.sql
-- DESCRIPCIÓN: Esquema inicial completo de INSERTIC SAS
-- VERSIÓN: 1.0.0
-- FECHA: 2026-02-27
-- ══════════════════════════════════════════════════════════════════════════════

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: leads
-- DESCRIPCIÓN: Contactos interesados en servicios de INSERTIC
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    company VARCHAR(100),
    service_line VARCHAR(20) NOT NULL,
    zone VARCHAR(20) NOT NULL,
    source VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    score INTEGER NOT NULL DEFAULT 0,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_contact_at TIMESTAMP,

    CONSTRAINT chk_lead_score CHECK (score >= 0 AND score <= 100),
    CONSTRAINT chk_lead_service_line CHECK (service_line IN ('ENERGY', 'COMMUNICATION', 'SECURITY', 'INFRASTRUCTURE')),
    CONSTRAINT chk_lead_zone CHECK (zone IN ('CARTAGENA', 'COSTA_CARIBE', 'NACIONAL')),
    CONSTRAINT chk_lead_source CHECK (source IN ('CALCULATOR', 'CONTACT_FORM', 'CHATBOT', 'PHONE', 'EMAIL', 'REFERRAL', 'WEBSITE', 'SOCIAL_MEDIA')),
    CONSTRAINT chk_lead_status CHECK (status IN ('NEW', 'CONTACTED', 'QUALIFIED', 'QUOTED', 'NEGOTIATING', 'WON', 'LOST'))
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_lead_email ON leads(email);
CREATE INDEX idx_lead_phone ON leads(phone);
CREATE INDEX idx_lead_status ON leads(status);
CREATE INDEX idx_lead_service_line ON leads(service_line);
CREATE INDEX idx_lead_created_at ON leads(created_at);

-- Comentarios
COMMENT ON TABLE leads IS 'Leads - Contactos interesados en servicios de INSERTIC';
COMMENT ON COLUMN leads.score IS 'Score de calidad del lead (0-100)';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: tool_sessions (Padre)
-- DESCRIPCIÓN: Sesiones de herramientas/calculadoras (tabla padre)
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE tool_sessions (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT REFERENCES leads(id) ON DELETE SET NULL,
    tool_type VARCHAR(30) NOT NULL,
    service_line VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    metadata JSONB,

    CONSTRAINT chk_tool_type CHECK (tool_type IN ('RISK_CALCULATOR', 'CCTV_CALCULATOR', 'VOIP_ESTIMATOR', 'NETWORK_PLANNER', 'SERVER_SIZING')),
    CONSTRAINT chk_session_service_line CHECK (service_line IN ('ENERGY', 'COMMUNICATION', 'SECURITY', 'INFRASTRUCTURE'))
);

-- Índices
CREATE INDEX idx_tool_session_lead ON tool_sessions(lead_id);
CREATE INDEX idx_tool_session_type ON tool_sessions(tool_type);
CREATE INDEX idx_tool_session_created ON tool_sessions(created_at);
CREATE INDEX idx_tool_session_metadata ON tool_sessions USING GIN(metadata);

COMMENT ON TABLE tool_sessions IS 'Sesiones de herramientas (calculadoras, estimadores)';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: risk_calculator_sessions (Hija)
-- DESCRIPCIÓN: Sesiones específicas de calculadora de riesgo eléctrico
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE risk_calculator_sessions (
    id BIGINT PRIMARY KEY REFERENCES tool_sessions(id) ON DELETE CASCADE,
    monthly_revenue NUMERIC(15,2),
    employees INTEGER NOT NULL,
    avg_salary NUMERIC(10,2),
    outages_per_year INTEGER,
    hours_per_outage DOUBLE PRECISION,
    critical_data BOOLEAN,
    annual_loss NUMERIC(15,2),
    recommended_ups VARCHAR(100),
    risk_level VARCHAR(20),

    CONSTRAINT chk_risk_employees CHECK (employees > 0),
    CONSTRAINT chk_risk_level CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

-- Índices
CREATE INDEX idx_risk_calc_risk_level ON risk_calculator_sessions(risk_level);
CREATE INDEX idx_risk_calc_annual_loss ON risk_calculator_sessions(annual_loss);

COMMENT ON TABLE risk_calculator_sessions IS 'Sesiones de calculadora de riesgo eléctrico';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: quotes
-- DESCRIPCIÓN: Cotizaciones generadas
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_number VARCHAR(20) NOT NULL UNIQUE,
    lead_id BIGINT NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    service_line VARCHAR(20) NOT NULL,
    subtotal NUMERIC(15,2) NOT NULL DEFAULT 0,
    tax NUMERIC(15,2) NOT NULL DEFAULT 0,
    discount NUMERIC(15,2) DEFAULT 0,
    total NUMERIC(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    valid_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    accepted_at TIMESTAMP,
    origin_session_id BIGINT REFERENCES tool_sessions(id) ON DELETE SET NULL,

    CONSTRAINT chk_quote_service_line CHECK (service_line IN ('ENERGY', 'COMMUNICATION', 'SECURITY', 'INFRASTRUCTURE')),
    CONSTRAINT chk_quote_status CHECK (status IN ('DRAFT', 'SENT', 'VIEWED', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    CONSTRAINT chk_quote_amounts CHECK (subtotal >= 0 AND tax >= 0 AND total >= 0)
);

-- Índices
CREATE INDEX idx_quote_lead ON quotes(lead_id);
CREATE INDEX idx_quote_number ON quotes(quote_number);
CREATE INDEX idx_quote_status ON quotes(status);
CREATE INDEX idx_quote_created ON quotes(created_at);

COMMENT ON TABLE quotes IS 'Cotizaciones generadas para leads';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: catalog_items (Padre)
-- DESCRIPCIÓN: Catálogo de productos y servicios (tabla padre)
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE catalog_items (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    service_line VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    base_cost NUMERIC(15,2) NOT NULL,
    sale_price NUMERIC(15,2) NOT NULL,
    margin NUMERIC(5,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    specifications JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_catalog_service_line CHECK (service_line IN ('ENERGY', 'COMMUNICATION', 'SECURITY', 'INFRASTRUCTURE')),
    CONSTRAINT chk_catalog_type CHECK (type IN ('PRODUCT', 'SERVICE', 'LABOR', 'OTHER')),
    CONSTRAINT chk_catalog_prices CHECK (base_cost >= 0 AND sale_price >= 0)
);

-- Índices
CREATE INDEX idx_catalog_sku ON catalog_items(sku);
CREATE INDEX idx_catalog_service_line ON catalog_items(service_line);
CREATE INDEX idx_catalog_type ON catalog_items(type);
CREATE INDEX idx_catalog_active ON catalog_items(active);
CREATE INDEX idx_catalog_specs ON catalog_items USING GIN(specifications);

COMMENT ON TABLE catalog_items IS 'Catálogo de productos y servicios';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: products (Hija)
-- DESCRIPCIÓN: Productos físicos del catálogo
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE products (
    id BIGINT PRIMARY KEY REFERENCES catalog_items(id) ON DELETE CASCADE,
    brand VARCHAR(100),
    model VARCHAR(100),
    stock INTEGER NOT NULL DEFAULT 0,
    min_stock INTEGER DEFAULT 0,
    supplier VARCHAR(100),

    CONSTRAINT chk_product_stock CHECK (stock >= 0 AND min_stock >= 0)
);

-- Índices
CREATE INDEX idx_product_brand ON products(brand);
CREATE INDEX idx_product_stock ON products(stock);

COMMENT ON TABLE products IS 'Productos físicos (UPS, equipos, etc)';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: services (Hija)
-- DESCRIPCIÓN: Servicios del catálogo
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE services (
    id BIGINT PRIMARY KEY REFERENCES catalog_items(id) ON DELETE CASCADE,
    frequency VARCHAR(20),
    estimated_hours INTEGER,
    recurring BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_service_frequency CHECK (frequency IN ('ONE_TIME', 'MONTHLY', 'QUARTERLY', 'BIANNUAL', 'ANNUAL'))
);

COMMENT ON TABLE services IS 'Servicios (mantenimiento, instalación, etc)';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: quote_items
-- DESCRIPCIÓN: Items individuales de cada cotización
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE quote_items (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL REFERENCES quotes(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    catalog_item_id BIGINT REFERENCES catalog_items(id) ON DELETE SET NULL,
    description VARCHAR(500) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL,
    discount NUMERIC(15,2) DEFAULT 0,
    total NUMERIC(15,2) NOT NULL,
    metadata JSONB,

    CONSTRAINT chk_quote_item_type CHECK (type IN ('PRODUCT', 'SERVICE', 'LABOR', 'OTHER')),
    CONSTRAINT chk_quote_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_quote_item_prices CHECK (unit_price >= 0 AND total >= 0)
);

-- Índices
CREATE INDEX idx_quote_item_quote ON quote_items(quote_id);
CREATE INDEX idx_quote_item_catalog ON quote_items(catalog_item_id);

COMMENT ON TABLE quote_items IS 'Items de cotizaciones';

-- ══════════════════════════════════════════════════════════════════════════════
-- TABLA: interactions
-- DESCRIPCIÓN: Registro de interacciones con leads
-- ══════════════════════════════════════════════════════════════════════════════
CREATE TABLE interactions (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    outcome VARCHAR(20),
    notes TEXT,
    scheduled_for TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_interaction_type CHECK (type IN ('CALL', 'EMAIL', 'WHATSAPP', 'VISIT', 'NOTE')),
    CONSTRAINT chk_interaction_outcome CHECK (outcome IN ('SUCCESSFUL', 'NO_ANSWER', 'SCHEDULED', 'LOST'))
);

-- Índices
CREATE INDEX idx_interaction_lead ON interactions(lead_id);
CREATE INDEX idx_interaction_type ON interactions(type);
CREATE INDEX idx_interaction_created ON interactions(created_at);
CREATE INDEX idx_interaction_scheduled ON interactions(scheduled_for);

COMMENT ON TABLE interactions IS 'Historial de interacciones con leads';

-- ══════════════════════════════════════════════════════════════════════════════
-- SEED DATA - PRODUCTOS UPS
-- ══════════════════════════════════════════════════════════════════════════════

-- UPS Oficina 1500VA
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'UPS-OFF-001',
    'UPS Oficina 1500VA',
    'UPS para oficinas pequeñas de 1-10 empleados. Potencia 1500VA/900W con respaldo de 15 minutos. Ideal para equipos básicos y protección de datos.',
    'ENERGY',
    'PRODUCT',
    2500000,
    3500000,
    28.57,
    true,
    '{"power_va": 1500, "power_w": 900, "backup_time_min": 15, "outlets": 6, "warranty_years": 2}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO products (id, brand, model, stock, min_stock, supplier)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'UPS-OFF-001'),
    'APC',
    'BR1500MS',
    5,
    2,
    'Schneider Electric Colombia'
);

-- UPS Empresarial 6KVA
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'UPS-EMP-001',
    'UPS Empresarial 6KVA',
    'UPS para empresas medianas de 11-50 empleados. Potencia 6KVA con respaldo de 30 minutos. Incluye software de gestión remota.',
    'ENERGY',
    'PRODUCT',
    11000000,
    15000000,
    26.67,
    true,
    '{"power_va": 6000, "power_w": 4800, "backup_time_min": 30, "outlets": 8, "warranty_years": 3, "remote_management": true}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO products (id, brand, model, stock, min_stock, supplier)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'UPS-EMP-001'),
    'APC',
    'SMX3000HV',
    3,
    1,
    'Schneider Electric Colombia'
);

-- UPS Industrial 20KVA
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'UPS-IND-001',
    'UPS Industrial 20KVA',
    'UPS industrial para empresas grandes de 50+ empleados. Potencia 20KVA con respaldo de 60 minutos. Sistema redundante N+1.',
    'ENERGY',
    'PRODUCT',
    44000000,
    60000000,
    26.67,
    true,
    '{"power_va": 20000, "power_w": 18000, "backup_time_min": 60, "outlets": 16, "warranty_years": 5, "remote_management": true, "redundancy": "N+1"}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO products (id, brand, model, stock, min_stock, supplier)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'UPS-IND-001'),
    'APC',
    'SURT20KRMXLI',
    1,
    1,
    'Schneider Electric Colombia'
);

-- Baterías de respaldo adicionales
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'BAT-EXT-001',
    'Batería Externa 24V',
    'Banco de baterías externas para extender tiempo de respaldo. Compatible con UPS empresarial e industrial.',
    'ENERGY',
    'PRODUCT',
    3500000,
    4800000,
    27.08,
    true,
    '{"voltage": 24, "capacity_ah": 100, "extended_runtime_min": 30}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO products (id, brand, model, stock, min_stock, supplier)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'BAT-EXT-001'),
    'APC',
    'SURT192XLBP',
    4,
    2,
    'Schneider Electric Colombia'
);

-- ══════════════════════════════════════════════════════════════════════════════
-- SEED DATA - SERVICIOS DE MANTENIMIENTO
-- ══════════════════════════════════════════════════════════════════════════════

-- Plan Básico
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'SVC-MANT-BAS',
    'Plan Mantenimiento Básico',
    'Mantenimiento preventivo semestral de UPS. Incluye: limpieza, revisión de baterías, actualización de firmware, reporte técnico.',
    'ENERGY',
    'SERVICE',
    450000,
    600000,
    25.00,
    true,
    '{"includes": ["cleaning", "battery_check", "firmware_update", "technical_report"], "response_time_hours": 48}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO services (id, frequency, estimated_hours, recurring)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'SVC-MANT-BAS'),
    'BIANNUAL',
    4,
    true
);

-- Plan Preventivo
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'SVC-MANT-PRE',
    'Plan Mantenimiento Preventivo',
    'Mantenimiento preventivo trimestral de UPS. Incluye: todo lo del plan básico + monitoreo remoto 24/7 + soporte prioritario.',
    'ENERGY',
    'SERVICE',
    1350000,
    1800000,
    25.00,
    true,
    '{"includes": ["cleaning", "battery_check", "firmware_update", "technical_report", "remote_monitoring", "priority_support"], "response_time_hours": 24}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO services (id, frequency, estimated_hours, recurring)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'SVC-MANT-PRE'),
    'QUARTERLY',
    6,
    true
);

-- Plan Crítico
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'SVC-MANT-CRI',
    'Plan Mantenimiento Crítico',
    'Mantenimiento mensual para sistemas críticos. Incluye: todo lo del plan preventivo + SLA de 4 horas + técnico dedicado + reemplazo express.',
    'ENERGY',
    'SERVICE',
    3600000,
    4800000,
    25.00,
    true,
    '{"includes": ["cleaning", "battery_check", "firmware_update", "technical_report", "remote_monitoring", "priority_support", "dedicated_tech", "express_replacement"], "response_time_hours": 4, "sla": "99.9%"}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO services (id, frequency, estimated_hours, recurring)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'SVC-MANT-CRI'),
    'MONTHLY',
    8,
    true
);

-- Servicio de Instalación
INSERT INTO catalog_items (sku, name, description, service_line, type, base_cost, sale_price, margin, active, specifications, created_at)
VALUES (
    'SVC-INST-001',
    'Instalación de UPS',
    'Instalación profesional de sistema UPS. Incluye: cableado, configuración, puesta en marcha, capacitación básica al personal.',
    'ENERGY',
    'SERVICE',
    800000,
    1200000,
    33.33,
    true,
    '{"includes": ["wiring", "configuration", "commissioning", "basic_training"], "travel_included_km": 50}'::jsonb,
    CURRENT_TIMESTAMP
);

INSERT INTO services (id, frequency, estimated_hours, recurring)
VALUES (
    (SELECT id FROM catalog_items WHERE sku = 'SVC-INST-001'),
    'ONE_TIME',
    6,
    false
);

-- ══════════════════════════════════════════════════════════════════════════════
-- VISTAS ÚTILES (Opcional)
-- ══════════════════════════════════════════════════════════════════════════════

-- Vista de leads con conteo de sesiones
CREATE VIEW v_leads_with_sessions AS
SELECT
    l.*,
    COUNT(ts.id) as total_sessions,
    MAX(ts.created_at) as last_session_at
FROM leads l
LEFT JOIN tool_sessions ts ON ts.lead_id = l.id
GROUP BY l.id;

-- Vista de productos con stock bajo
CREATE VIEW v_products_low_stock AS
SELECT
    ci.id,
    ci.sku,
    ci.name,
    p.stock,
    p.min_stock,
    ci.sale_price
FROM catalog_items ci
INNER JOIN products p ON p.id = ci.id
WHERE p.stock <= p.min_stock
  AND ci.active = true
ORDER BY p.stock ASC;

-- ══════════════════════════════════════════════════════════════════════════════
-- ESTADÍSTICAS INICIALES
-- ══════════════════════════════════════════════════════════════════════════════

-- Mostrar resumen de datos iniciales
DO $$
BEGIN
    RAISE NOTICE '════════════════════════════════════════════════════════════';
    RAISE NOTICE 'INSERTIC SAS - Base de datos inicializada exitosamente';
    RAISE NOTICE '════════════════════════════════════════════════════════════';
    RAISE NOTICE 'Tablas creadas: 9';
    RAISE NOTICE 'Productos UPS cargados: %', (SELECT COUNT(*) FROM products);
    RAISE NOTICE 'Servicios cargados: %', (SELECT COUNT(*) FROM services);
    RAISE NOTICE 'Items catálogo total: %', (SELECT COUNT(*) FROM catalog_items);
    RAISE NOTICE '════════════════════════════════════════════════════════════';
END $$;

-- ══════════════════════════════════════════════════════════════════════════════
-- FIN DE MIGRATION
-- ══════════════════════════════════════════════════════════════════════════════