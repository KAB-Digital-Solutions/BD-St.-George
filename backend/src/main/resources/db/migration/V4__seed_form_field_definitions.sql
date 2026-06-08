-- Baseline dynamic fields for members module (requirements §8 Phase 3)
INSERT INTO form_field_definitions
    (module_key, field_key, label_am, label_en, field_type, is_required, sort_order, is_system_core)
VALUES
    ('members', 'baptismal_name', 'የክርስትና ስም', 'Baptismal Name', 'TEXT', TRUE, 1, FALSE),
    ('members', 'birth_date', 'የልደት ዘመን', 'Birth Date', 'DATE', FALSE, 2, FALSE),
    ('members', 'birthplace', 'የትውልድ ቦታ', 'Birthplace', 'TEXT', FALSE, 3, FALSE),
    ('members', 'baptismal_church', 'ክርስትና የተነሱበት ቤ/ክ', 'Baptismal Church', 'TEXT', FALSE, 4, FALSE),
    ('members', 'house_number', 'የቤት ቁጥር', 'House Number', 'TEXT', FALSE, 5, FALSE),
    ('members', 'marital_status', 'የትዳር ሁኔታ', 'Marital Status', 'DROPDOWN', FALSE, 6, FALSE);

UPDATE form_field_definitions
SET dropdown_opts = '["ያላገባ","ያገባ","ፍቺ","መበለት","መበለተ ወንድ"]'::jsonb
WHERE module_key = 'members' AND field_key = 'marital_status';

-- Staff ministers defaults (FR-A06, FR-A07)
INSERT INTO form_field_definitions
    (module_key, field_key, label_am, label_en, field_type, is_required, sort_order)
VALUES
    ('staff_ministers', 'spiritual_education', 'መንፈሳዊ ትምህርት', 'Spiritual Education', 'TEXT', FALSE, 1),
    ('staff_ministers', 'theological_education', 'ኦርቶዶክስ ትምህርት', 'Theological Education', 'TEXT', FALSE, 2),
    ('staff_ministers', 'modern_education', 'ዘመናዊ ትምህርት', 'Modern Education', 'TEXT', FALSE, 3),
    ('staff_ministers', 'salary_diocese', 'ደመወዝ ከሀገረ ስብከት', 'Salary from Diocese', 'TEXT', FALSE, 4),
    ('staff_ministers', 'salary_parish', 'ደመወዝ ከቤተ ክርስቲያን', 'Salary from Parish', 'TEXT', FALSE, 5);

-- Family relationship dropdown (FR-B06)
INSERT INTO form_field_definitions
    (module_key, field_key, label_am, label_en, field_type, is_required, sort_order, dropdown_opts)
VALUES
    ('family_members', 'relationship_type', 'ዝምድና', 'Relationship', 'DROPDOWN', TRUE, 1,
     '["ባል","ሚስት","ልጅ","አባት","እናት","ሌላ"]'::jsonb);
