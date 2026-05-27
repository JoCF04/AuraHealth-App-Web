-- ── Roles ─────────────────────────────────────────────────────────────────────
INSERT INTO roles (id, name)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'ROLE_DOCTOR'),
    (2, 'ROLE_ADMIN'),
    (3, 'ROLE_USER')
ON CONFLICT (id) DO NOTHING;

-- ── Usuarios ──────────────────────────────────────────────────────────────────
INSERT INTO users (id, first_name, last_name, email, password_hash, birth_date, gender, is_email_verified, preferred_language, role, created_at)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'Omar',   'Rodrigo',    'omar@gmail.com',   '$2a$12$pb7bVAYdyV3dMSopGogQ4efDtLtTRMpof7x9j4bhKdp3kVSEKBLqG', '2004-05-20', 'male',   true, 'es', 'ROLE_DOCTOR', NOW()),
    (2, 'Masiel', 'Callañaupa', 'masiel@gmail.com', '$2a$12$13OujyTV.hizkXhEyWpNzu/qmNN8AUOv7rUGnl7jPRkAoMxrRVyZG', '2004-05-21', 'female', true, 'es', 'ROLE_ADMIN',  NOW()),
    (3, 'Lucia',  'Jimenez',    'lucia@gmail.com',  '$2a$12$kaD.PS.f6R7xUZrPWLiOwuHs028BgFx1EjB9BppacDAAtbOhoEMe6',  '2004-05-22', 'female', true, 'es', 'ROLE_USER',   NOW())
ON CONFLICT (id) DO NOTHING;

-- ── Asignación usuario ↔ rol ──────────────────────────────────────────────────
INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3)
ON CONFLICT (user_id, role_id) DO NOTHING;

-- ── Sincronizar secuencias ────────────────────────────────────────────────────
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));