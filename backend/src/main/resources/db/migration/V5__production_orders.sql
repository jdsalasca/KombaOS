CREATE TABLE production_orders (
    id VARCHAR(50) PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL,
    due_date DATE,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
