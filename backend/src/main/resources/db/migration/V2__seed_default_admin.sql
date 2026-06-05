-- Default dev admin: username admin / password ChangeMe123! (change before production)
INSERT INTO users (username, password_hash, full_name, role)
VALUES (
    'admin',
    '$2b$10$JNMbMZe.hvu3LVmTHmD22ucRSzb38mNlj1lRzfFk6Uy8CSXwLX.Ue',
    'System Administrator',
    'ADMIN'
);
