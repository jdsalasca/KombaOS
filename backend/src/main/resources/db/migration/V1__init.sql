create table if not exists materials (
  id varchar(64) primary key,
  name varchar(200) not null,
  unit varchar(50) not null,
  created_at timestamp not null
);

create table if not exists inventory_movements (
  id varchar(64) primary key,
  material_id varchar(64) not null,
  type varchar(20) not null,
  quantity numeric(19,6) not null,
  reason varchar(200),
  created_at timestamp not null
);
