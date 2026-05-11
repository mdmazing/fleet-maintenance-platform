CREATE TABLE technicians (
    id               BIGSERIAL PRIMARY KEY,
    employee_number  VARCHAR(50)  NOT NULL UNIQUE,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    phone            VARCHAR(30),
    specialization   VARCHAR(100),
    available        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
