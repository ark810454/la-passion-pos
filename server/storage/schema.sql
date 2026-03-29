create table if not exists app_settings (
  singleton_key text primary key,
  payload jsonb not null
);

create table if not exists products (
  id text primary key,
  name text not null,
  price integer not null default 0,
  category text not null default 'misc',
  active boolean not null default true,
  payload jsonb not null,
  updated_at timestamptz not null default now()
);

create table if not exists dining_tables (
  id text primary key,
  status text not null default 'free',
  payload jsonb not null,
  updated_at timestamptz not null default now()
);

create table if not exists app_users (
  id text primary key,
  name text not null,
  role text not null,
  pin text not null,
  payload jsonb not null,
  updated_at timestamptz not null default now()
);

create table if not exists orders (
  id text primary key,
  source text,
  table_id text,
  payment_method text,
  total numeric not null default 0,
  status text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz,
  items jsonb not null default '[]'::jsonb,
  payload jsonb not null
);
