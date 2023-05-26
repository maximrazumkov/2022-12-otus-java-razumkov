create table client
(
    id   bigserial not null primary key,
    name varchar(50),
    address_id bigint
);

create table phone
(
    id        bigserial not null primary key,
    number    varchar(50),
    client_id bigint
);

create table address
(
    id     bigserial not null primary key,
    street varchar(50)
);