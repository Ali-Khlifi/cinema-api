-- Migration to populate cinema address fields from existing street_name data
-- Extracts address components into separate fields: street_number, street_name, postal_code, city

-- Cinema Parisien (id=1) - Paris
UPDATE cinema 
SET 
    street_number = '123',
    street_name = 'Avenue des Champs-Élysées',
    postal_code = '75008',
    city = 'Paris'
WHERE id = 1;

-- Cinema Lyonnais (id=2) - Lyon
UPDATE cinema 
SET 
    street_number = '45',
    street_name = 'Rue de la République',
    postal_code = '69002',
    city = 'Lyon'
WHERE id = 2;

-- Cinema Marseillais (id=3) - Marseille
UPDATE cinema 
SET 
    street_number = '78',
    street_name = 'La Canebière',
    postal_code = '13001',
    city = 'Marseille'
WHERE id = 3;

-- Cinema Toulousain (id=4) - Toulouse
UPDATE cinema 
SET 
    street_number = '56',
    street_name = 'Rue Alsace-Lorraine',
    postal_code = '31000',
    city = 'Toulouse'
WHERE id = 4;

-- UGC Cité Internationale (id=5) - Lyon
UPDATE cinema 
SET 
    street_number = '80',
    street_name = 'Quai Charles de Gaulle',
    postal_code = '69006',
    city = 'Lyon'
WHERE id = 5;

-- UGC Cité Confluence (id=6) - Lyon
UPDATE cinema 
SET 
    street_number = '112',
    street_name = 'Cours Charlemagne',
    postal_code = '69002',
    city = 'Lyon'
WHERE id = 6;

-- UGC Astoria (id=7) - Lyon
UPDATE cinema 
SET 
    street_number = '31',
    street_name = 'Cours Vitton',
    postal_code = '69006',
    city = 'Lyon'
WHERE id = 7;

-- UGC Cité Part-dieu (id=8) - Lyon
UPDATE cinema 
SET 
    street_number = '',
    street_name = 'Centre Commercial La Part-Dieu - Niveau 3',
    postal_code = '69003',
    city = 'Lyon'
WHERE id = 8;

