CREATE TABLE sales_orders (
    id VARCHAR(50) PRIMARY KEY,
    customer_name VARCHAR(200) NOT NULL,
    customer_email VARCHAR(200) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
