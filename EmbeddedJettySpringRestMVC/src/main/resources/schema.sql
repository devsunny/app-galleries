create table Resources
(
	name varchar(128) not null primary key,
	maxCapacity double not null default 100.0,
	reservedCapacity double not null default 0
)
;

