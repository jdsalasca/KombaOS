CREATE TABLE survey_templates (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    question VARCHAR(1000) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE survey_responses (
    id VARCHAR(50) PRIMARY KEY,
    template_id VARCHAR(50) NOT NULL,
    customer_email VARCHAR(200) NOT NULL,
    score INTEGER NOT NULL,
    comment VARCHAR(2000),
    created_at TIMESTAMP NOT NULL
);
