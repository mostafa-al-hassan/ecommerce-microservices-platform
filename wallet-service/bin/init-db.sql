IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'auth_db')
BEGIN
    CREATE DATABASE auth_db;
END
GO
