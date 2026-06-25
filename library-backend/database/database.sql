CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    quantity INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'available',
    category VARCHAR(50),
    cover_url TEXT,
    open_library_id VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS borrowings (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    book_id INT REFERENCES books(id),
    borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'borrowed'
);

CREATE TABLE IF NOT EXISTS book_requests (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    book_title VARCHAR(255),
    book_author VARCHAR(255),
    open_library_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Insert the admin account only if it doesn't already exist
INSERT INTO "users" (id, username, password, email, created_at, role)
VALUES (
    6767, 
    'admin123', 
    crypt('admin123', gen_salt('bf', 10)), 
    'admin123@gmail.com', 
    CURRENT_DATE, 
    'admin'
)
ON CONFLICT (id) DO NOTHING; 