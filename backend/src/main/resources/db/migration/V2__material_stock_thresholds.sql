create table if not exists material_stock_thresholds (
  material_id varchar(64) primary key,
  min_stock numeric(19,6) not null,
  updated_at timestamp not null
);
